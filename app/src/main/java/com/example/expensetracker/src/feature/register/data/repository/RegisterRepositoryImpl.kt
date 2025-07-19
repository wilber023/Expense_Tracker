package com.example.expensetracker.src.feature.register.data.repository

import android.util.Log
import com.example.expensetracker.src.register.data.dataSource.remote.RegisterApi
import com.example.expensetracker.src.register.data.dataSource.remote.RegisterRequest
import com.example.expensetracker.src.feature.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val api: RegisterApi
) : RegisterRepository {

    override suspend fun registerUser(username: String, pin: String): Boolean {
        return try {
            Log.d("RegisterRepository", "  Registrando usuario: '$username'")

            val request = RegisterRequest(username, pin)
            val response = api.register(request)

            Log.d("RegisterRepository", "  Response code: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("RegisterRepository", " Response body: $body")

                if (body?.success == true) {
                    Log.d("RegisterRepository", " Registro exitoso")
                    true
                } else {
                    Log.e("RegisterRepository", "  Registro falló: ${body?.message}")
                    throw Exception(body?.message ?: "Error en el registro")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("RegisterRepository", "  HTTP Error: ${response.code()}")
                Log.e("RegisterRepository", "  Error body: $errorBody")

                val errorMessage = when (response.code()) {
                    409 -> "El usuario ya existe"
                    400 -> "Datos de registro inválidos"
                    500 -> "Error interno del servidor"
                    else -> "Error de registro: ${response.code()}"
                }

                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("RegisterRepository", "  Error en registro: ${e.message}")


            val friendlyMessage = when {
                e.message?.contains("Unable to resolve host") == true -> "Error de conexión. Verifica tu red."
                e.message?.contains("Connection refused") == true -> "No se puede conectar al servidor."
                e.message?.contains("timeout") == true -> "Tiempo de espera agotado. Intenta de nuevo."
                else -> e.message ?: "Error de conexión"
            }

            throw Exception(friendlyMessage)
        }
    }
}