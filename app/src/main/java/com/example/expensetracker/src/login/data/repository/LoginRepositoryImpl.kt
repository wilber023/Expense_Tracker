package com.example.expensetracker.src.login.data.repository

import android.util.Log
import com.example.expensetracker.src.login.data.dataSource.remote.LoginFetch
import com.example.expensetracker.src.login.domain.repository.LoginRepository
import com.example.expensetracker.src.login.domain.model.LoginResult

class LoginRepositoryImpl(
    private val loginFetch: LoginFetch
) : LoginRepository {

    override suspend fun validateUser(username: String, pin: String): LoginResult {
        return try {
            Log.d("LoginRepository", "Validando usuario: '$username' con PIN de ${pin.length} dígitos")

            val response = loginFetch.login(username, pin)

            if (response.isSuccess) {
                val loginResponse = response.getOrNull()
                if (loginResponse?.success == true) {
                    Log.d("LoginRepository", "Login exitoso")
                    Log.d("LoginRepository", "Token recibido del servidor: ${loginResponse.token}")

                    LoginResult(
                        success = true,
                        token = loginResponse.token,
                        message = loginResponse.message ?: "Login exitoso"
                    )
                } else {
                    Log.e("LoginRepository", "Credenciales incorrectas: ${loginResponse?.message}")
                    LoginResult(
                        success = false,
                        message = loginResponse?.message ?: "Credenciales incorrectas"
                    )
                }
            } else {
                val error = response.exceptionOrNull()
                Log.e("LoginRepository", "Error de conexión: ${error?.message}")
                LoginResult(
                    success = false,
                    message = "Error de conexión"
                )
            }
        } catch (e: Exception) {
            Log.e("LoginRepository", "Excepción en validateUser: ${e.message}", e)
            LoginResult(
                success = false,
                message = e.message ?: "Error desconocido"
            )
        }
    }
}