package com.example.expensetracker.src.login.data.dataSource.remote

import android.util.Log
import com.example.expensetracker.src.login.data.dataSource.local.remote.LoginApi
import com.example.expensetracker.src.login.data.dataSource.local.remote.LoginRequest
import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.login.data.dataSource.local.remote.LoginResponse

class LoginFetch(private val api: LoginApi = NetworkModule.loginApi) {

    suspend fun login(username: String, pin: String): Result<LoginResponse> {
        return try {
            Log.d("LoginFetch", "Enviando request - Usuario: $username, PIN: $pin")

            val response = api.login(LoginRequest(username, pin))

            Log.d("LoginFetch", "Response code: ${response.code()}")
            Log.d("LoginFetch", "Response message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("LoginFetch", "Response body: $body")

                if (body != null) {
                    Log.d("LoginFetch", "Success: ${body.success}")
                    Log.d("LoginFetch", "Token: '${body.token}'")
                    Log.d("LoginFetch", "Message: ${body.message}")

                    Result.success(body)
                } else {
                    Log.e("LoginFetch", "Body es null")
                    Result.success(LoginResponse(
                        success = false,
                        message = "Respuesta vacía del servidor"
                    ))
                }
            } else {
                Log.e("LoginFetch", "Error HTTP: ${response.code()} - ${response.message()}")


                val errorBody = response.errorBody()?.string()
                Log.e("LoginFetch", "Error body: $errorBody")

                Result.failure(Exception("Error de autenticación: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("LoginFetch", "Error de red: ${e.message}", e)
            Result.failure(e)
        }
    }
}