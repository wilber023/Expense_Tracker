package com.example.expensetracker.src.core.network

import android.content.Context
import android.util.Log
import com.example.expensetracker.src.core.dataStore.DataStoreToken
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context? = null) : Interceptor {


    private val dataStoreToken by lazy {
        if (context != null) {
            try {
                DataStoreToken.getInstance(context)
            } catch (e: Exception) {
                Log.w("AuthInterceptor", "Error creando DataStoreToken: ${e.message}")
                null
            }
        } else null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var token: String? = null




        token = TokenManager.getToken()
        if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", " Token obtenido de TokenManager")
        } else {
            Log.d("AuthInterceptor", " No hay token en TokenManager")


            if (dataStoreToken != null) {
                try {
                    token = runBlocking { dataStoreToken!!.getToken() }
                    if (!token.isNullOrEmpty()) {
                        Log.d("AuthInterceptor", " Token obtenido de DataStore")
                        TokenManager.setToken(token)
                        Log.d("AuthInterceptor", " Token sincronizado a TokenManager")
                    } else {
                        Log.d("AuthInterceptor", "️ DataStore no tiene token")
                    }
                } catch (e: Exception) {
                    Log.e("AuthInterceptor", " Error accediendo a DataStore: ${e.message}")
                }
            } else {
                Log.d("AuthInterceptor", " DataStore no disponible")
            }
        }

        val request = if (token != null && token.isNotEmpty()) {
            Log.d("AuthInterceptor", " Agregando Authorization header")
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w("AuthInterceptor", " No hay token disponible - enviando request sin autenticación")
            chain.request()
        }

        return chain.proceed(request)
    }
}