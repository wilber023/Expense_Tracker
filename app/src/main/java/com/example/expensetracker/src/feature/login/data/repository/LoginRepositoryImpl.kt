package com.example.expensetracker.src.feature.login.data.repository

import android.content.Context
import android.util.Log
import com.example.expensetracker.src.core.dataStore.DataStoreToken
import com.example.expensetracker.src.core.network.TokenManager
import com.example.expensetracker.src.login.data.dataSource.remote.LoginFetch
import com.example.expensetracker.src.feature.login.domain.repository.LoginRepository
import com.example.expensetracker.src.feature.login.domain.model.LoginResult

class LoginRepositoryImpl(
    private val loginFetch: LoginFetch,
    private val context: Context? = null
) : LoginRepository {

    private val dataStoreToken by lazy {
        if (context != null) DataStoreToken.getInstance(context) else null
    }

    override suspend fun validateUser(username: String, pin: String): LoginResult {
        return try {
            Log.d("LoginRepository", "  Validando usuario: '$username'")

            val response = loginFetch.login(username, pin)

            if (response.isSuccess) {
                val loginResponse = response.getOrNull()
                if (loginResponse?.success == true) {
                    Log.d("LoginRepository", "  Login exitoso")


                    loginResponse.token?.let { token ->

                        TokenManager.setToken(token)
                        Log.d("LoginRepository", "  Token guardado en TokenManager")


                        if (dataStoreToken != null) {
                            try {
                                dataStoreToken!!.saveToken(token)
                                Log.d("LoginRepository", "  Token guardado en DataStore")
                            } catch (e: Exception) {
                                Log.w("LoginRepository", "  Error guardando en DataStore: ${e.message}")

                            }
                        } else {
                            Log.d("LoginRepository", "  DataStore no disponible, usando solo TokenManager")
                        }
                    }

                    LoginResult(
                        success = true,
                        token = loginResponse.token,
                        message = loginResponse.message ?: "Login exitoso"
                    )
                } else {
                    Log.e("LoginRepository", " Credenciales incorrectas: ${loginResponse?.message}")
                    LoginResult(
                        success = false,
                        message = loginResponse?.message ?: "Credenciales incorrectas"
                    )
                }
            } else {
                val error = response.exceptionOrNull()
                Log.e("LoginRepository", "  Error de conexión: ${error?.message}")
                LoginResult(
                    success = false,
                    message = "Error de conexión"
                )
            }
        } catch (e: Exception) {
            Log.e("LoginRepository", "  Excepción en validateUser: ${e.message}", e)
            LoginResult(
                success = false,
                message = e.message ?: "Error desconocido"
            )
        }
    }

    override suspend fun getStoredToken(): String? {
        return dataStoreToken?.getToken() ?: TokenManager.getToken()
    }

    override suspend fun clearToken() {
        TokenManager.clearToken()
        dataStoreToken?.clearToken()
        Log.d("LoginRepository", " Tokens eliminados")
    }
}