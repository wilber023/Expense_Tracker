package com.example.expensetracker.src.core.token

import android.content.Context
import android.util.Log
import com.example.expensetracker.src.core.dataStore.DataStoreToken
import com.example.expensetracker.src.core.network.TokenManager

class TokenRepository private constructor(private val context: Context) {

    private val dataStoreToken by lazy {
        DataStoreToken.getInstance(context)
    }

    companion object {
        @Volatile
        private var INSTANCE: TokenRepository? = null

        fun getInstance(context: Context): TokenRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    suspend fun getToken(): String? {

        var token = TokenManager.getToken()

        if (!token.isNullOrEmpty()) {
            Log.d("TokenRepository", " Token obtenido de TokenManager")
            return token
        }


        try {
            token = dataStoreToken.getToken()
            if (!token.isNullOrEmpty()) {
                Log.d("TokenRepository", " Token obtenido de DataStore")

                TokenManager.setToken(token)
                Log.d("TokenRepository", " Token sincronizado a TokenManager")
                return token
            }
        } catch (e: Exception) {
            Log.e("TokenRepository", " Error obteniendo token de DataStore: ${e.message}")
        }

        Log.w("TokenRepository", " No hay token disponible en ningún lugar")
        return null
    }



    suspend fun clearToken() {
        TokenManager.clearToken()
        try {
            dataStoreToken.clearToken()
            Log.d("TokenRepository", " Token eliminado de ambos lugares")
        } catch (e: Exception) {
            Log.e("TokenRepository", " Error eliminando de DataStore: ${e.message}")
            Log.d("TokenRepository", " Token eliminado solo de TokenManager")
        }
    }
}