package com.example.expensetracker.src.home.data.dataSource.local.remote

import android.util.Log
import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.core.network.TokenManager

class ExpenseFetch(private val api: ExpenseApi = NetworkModule.expenseApi) {

    suspend fun addExpense(
        category: String,
        description: String,
        amount: Double,
        date: String
    ): Result<Boolean> {
        return try {
            val token = TokenManager.getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val request = ExpenseRequest(
                category = category,
                description = description,
                amount = amount,
                date = date
            )

            Log.d("ExpenseFetch", "Enviando request: $request")
            val response = api.addExpense("Bearer $token", request)

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body?.success == true) {
                        Log.d("ExpenseFetch", "Gasto creado exitosamente")
                        Result.success(true)
                    } else {
                        Log.e("ExpenseFetch", "Error: ${body?.message}")
                        Result.failure(Exception(body?.message ?: "Error desconocido"))
                    }
                }
                response.code() == 401 -> {
                    Log.e("ExpenseFetch", "HTTP Error: ${response.code()}")
                    TokenManager.clearToken()
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
            val token = TokenManager.getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token para getAllExpenses")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d("ExpenseFetch", "Iniciando getAllExpenses con token: ${token.take(10)}...")

            val response = api.getAllExpenses("Bearer $token")

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
                    TokenManager.clearToken()
                    Result.failure(Exception("Sesión expirada"))
                } else {
                    Result.failure(Exception("Error al obtener los gastos: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("ExpenseFetch", "Excepción en getAllExpenses: ${e.message}")
            Log.e("ExpenseFetch", "Stack trace: ", e)
            Result.failure(e)
        }
    }
    suspend fun updateExpense(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String
    ): Result<Boolean> {
        return try {
            val token = TokenManager.getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token para updateExpense")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val request = ExpenseRequest(
                category = category,
                description = description,
                amount = amount,
                date = date
            )

            Log.d("ExpenseFetch", "Actualizando gasto con ID: $id, request: $request")
            val response = api.updateExpense("Bearer $token", id, request)

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body?.success == true) {
                        Log.d("ExpenseFetch", "Gasto actualizado exitosamente")
                        Result.success(true)
                    } else {
                        Log.e("ExpenseFetch", "Error al actualizar: ${body?.message}")
                        Result.failure(Exception(body?.message ?: "Error desconocido"))
                    }
                }
                response.code() == 401 -> {
                    Log.e("ExpenseFetch", "HTTP Error: ${response.code()}")
                    TokenManager.clearToken()
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
            val token = TokenManager.getToken()
            if (token == null) {
                Log.e("ExpenseFetch", "No hay token para deleteExpense")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d("ExpenseFetch", "Eliminando gasto con ID: $id")
            val response = api.deleteExpense("Bearer $token", id)

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
                    Log.e("ExpenseFetch", "HTTP Error: ${response.code()}")
                    TokenManager.clearToken()
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