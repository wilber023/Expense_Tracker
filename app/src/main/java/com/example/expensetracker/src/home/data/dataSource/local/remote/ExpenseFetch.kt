package com.example.expensetracker.src.home.data.dataSource.local.remote
import com.example.expensetracker.src.core.hardware.domain.LocationData
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.core.token.TokenRepository
import com.example.expensetracker.src.home.domain.UseCase.ImageProcessingUseCase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ExpenseFetch(
    private val api: ExpenseApi = NetworkModule.expenseApi,
    private val context: Context? = null
) {

    private val tokenRepository by lazy {
        if (context != null) {
            TokenRepository.getInstance(context)
        } else null
    }

    private fun getImageProcessingUseCase(): ImageProcessingUseCase? {
        return if (context != null) {
            ImageProcessingUseCase(context)
        } else null
    }

    private suspend fun getToken(): String? {
        return if (tokenRepository != null) {
            tokenRepository!!.getToken()
        } else {
            com.example.expensetracker.src.core.network.TokenManager.getToken()
        }
    }

    suspend fun addExpense(
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null,
        location: LocationData? = null
    ): Result<Boolean> {
        return try {
            val token = getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token disponible")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d("ExpenseFetch", "Token encontrado, enviando request...")

            // Crear RequestBody para los campos de texto
            val textMediaType = "text/plain".toMediaTypeOrNull()
            val categoryBody = category.toRequestBody(textMediaType)
            val descriptionBody = description.toRequestBody(textMediaType)
            val amountBody = amount.toString().toRequestBody(textMediaType)
            val dateBody = date.toRequestBody(textMediaType)

            // Procesar imagen si existe
            val imagePart = if (imageUri != null) {
                val imageUseCase = getImageProcessingUseCase()
                if (imageUseCase != null) {
                    val result = imageUseCase.uriToMultipart(imageUri)
                    if (result.isSuccess) {
                        result.getOrNull()
                    } else {
                        Log.w("ExpenseFetch", "Error procesando imagen: ${result.exceptionOrNull()?.message}")
                        null
                    }
                } else {
                    Log.w("ExpenseFetch", "ImageProcessingUseCase no disponible")
                    null
                }
            } else null

            Log.d("ExpenseFetch", "Enviando request con imagen: ${imagePart != null}")

            val latitudeBody = location?.latitude?.toString()?.toRequestBody(textMediaType)
            val longitudeBody = location?.longitude?.toString()?.toRequestBody(textMediaType)
            val addressBody = location?.address?.toRequestBody(textMediaType)

            val response = api.addExpense(
                category = categoryBody,
                description = descriptionBody,
                amount = amountBody,
                date = dateBody,
                image = imagePart,
                latitude = latitudeBody,      // ← Agregar
                longitude = longitudeBody,    // ← Agregar
                address = addressBody
            )
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body?.success == true) {
                        Log.d("ExpenseFetch", "Gasto creado exitosamente")
                        // Limpiar archivos temporales
                        getImageProcessingUseCase()?.cleanupTempFiles()
                        Result.success(true)
                    } else {
                        Log.e("ExpenseFetch", "Error: ${body?.message}")
                        Result.failure(Exception(body?.message ?: "Error desconocido"))
                    }
                }
                response.code() == 401 -> {
                    Log.e("ExpenseFetch", "HTTP Error 401: Token inválido")
                    tokenRepository?.clearToken() ?: com.example.expensetracker.src.core.network.TokenManager.clearToken()
                    Result.failure(Exception("Sesión expirada"))
                }
                else -> {
                    Log.e("ExpenseFetch", "HTTP Error: ${response.code()}")
                    Result.failure(Exception("Error de conexión"))
                }
            }
        } catch (e: Exception) {
            Log.e("ExpenseFetch", "Excepción: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllExpenses(): Result<List<ExpenseData>> {
        return try {
            val token = getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token para getAllExpenses")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d("ExpenseFetch", "Iniciando getAllExpenses con token válido")

            val response = api.getAllExpenses()

            Log.d("ExpenseFetch", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("ExpenseFetch", "Response body: $responseBody")

                if (responseBody?.success == true) {
                    val expenses = responseBody.data
                    Log.d("ExpenseFetch", "Gastos obtenidos: ${expenses.size} elementos")

                    expenses.forEach { expense ->
                        Log.d("ExpenseFetch", "Gasto: $expense")
                    }

                    Result.success(expenses)
                } else {
                    Log.e("ExpenseFetch", "API response success = false")
                    Result.failure(Exception("Error en la respuesta del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ExpenseFetch", "Error HTTP: ${response.code()}")
                Log.e("ExpenseFetch", "Error body: $errorBody")

                if (response.code() == 401) {
                    tokenRepository?.clearToken() ?: com.example.expensetracker.src.core.network.TokenManager.clearToken()
                    Result.failure(Exception("Sesión expirada"))
                } else {
                    Result.failure(Exception("Error al obtener los gastos: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("ExpenseFetch", "Excepción en getAllExpenses: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateExpense(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null,
        location: LocationData? = null
    ): Result<Boolean> {
        return try {
            val token = getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token para updateExpense")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            // Crear RequestBody para los campos de texto
            val textMediaType = "text/plain".toMediaTypeOrNull()
            val categoryBody = category.toRequestBody(textMediaType)
            val descriptionBody = description.toRequestBody(textMediaType)
            val amountBody = amount.toString().toRequestBody(textMediaType)
            val dateBody = date.toRequestBody(textMediaType)

            // Procesar imagen si existe
            val imagePart = if (imageUri != null) {
                val imageUseCase = getImageProcessingUseCase()
                if (imageUseCase != null) {
                    val result = imageUseCase.uriToMultipart(imageUri)
                    if (result.isSuccess) {
                        result.getOrNull()
                    } else {
                        Log.w("ExpenseFetch", "Error procesando imagen: ${result.exceptionOrNull()?.message}")
                        null
                    }
                } else {
                    Log.w("ExpenseFetch", "ImageProcessingUseCase no disponible")
                    null
                }
            } else null

            Log.d("ExpenseFetch", "Actualizando gasto con ID: $id")

            val latitudeBody = location?.latitude?.toString()?.toRequestBody(textMediaType)
            val longitudeBody = location?.longitude?.toString()?.toRequestBody(textMediaType)
            val addressBody = location?.address?.toRequestBody(textMediaType)

            val response = api.updateExpense(
                id = id,
                category = categoryBody,
                description = descriptionBody,
                amount = amountBody,
                date = dateBody,
                image = imagePart,
                latitude = latitudeBody,    // ← Agregar
                longitude = longitudeBody,  // ← Agregar
                address = addressBody       // ← Agregar
            )
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body?.success == true) {
                        Log.d("ExpenseFetch", "Gasto actualizado exitosamente")
                        // Limpiar archivos temporales
                        getImageProcessingUseCase()?.cleanupTempFiles()
                        Result.success(true)
                    } else {
                        Log.e("ExpenseFetch", "Error al actualizar: ${body?.message}")
                        Result.failure(Exception(body?.message ?: "Error desconocido"))
                    }
                }
                response.code() == 401 -> {
                    Log.e("ExpenseFetch", "HTTP Error 401: Token inválido")
                    tokenRepository?.clearToken() ?: com.example.expensetracker.src.core.network.TokenManager.clearToken()
                    Result.failure(Exception("Sesión expirada"))
                }
                else -> {
                    Log.e("ExpenseFetch", "HTTP Error: ${response.code()}")
                    Result.failure(Exception("Error de conexión al actualizar"))
                }
            }
        } catch (e: Exception) {
            Log.e("ExpenseFetch", "Excepción en updateExpense: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(id: String): Result<Boolean> {
        return try {
            val token = getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token para deleteExpense")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d("ExpenseFetch", "Eliminando gasto con ID: $id")
            val response = api.deleteExpense(id)

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body?.success == true) {
                        Log.d("ExpenseFetch", "Gasto eliminado exitosamente")
                        Result.success(true)
                    } else {
                        Log.e("ExpenseFetch", "Error al eliminar: ${body?.message}")
                        Result.failure(Exception(body?.message ?: "Error desconocido"))
                    }
                }
                response.code() == 401 -> {
                    Log.e("ExpenseFetch", "HTTP Error 401: Token inválido")
                    tokenRepository?.clearToken() ?: com.example.expensetracker.src.core.network.TokenManager.clearToken()
                    Result.failure(Exception("Sesión expirada"))
                }
                response.code() == 404 -> {
                    Log.e("ExpenseFetch", "Gasto no encontrado")
                    Result.failure(Exception("Gasto no encontrado"))
                }
                else -> {
                    Log.e("ExpenseFetch", "HTTP Error: ${response.code()}")
                    Result.failure(Exception("Error de conexión al eliminar"))
                }
            }
        } catch (e: Exception) {
            Log.e("ExpenseFetch", "Excepción en deleteExpense: ${e.message}")
            Result.failure(e)
        }
    }
}