package com.example.expensetracker.src.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken()

        Log.d("AuthInterceptor", "Token disponible: $token")

        val request = if (token != null && token.isNotEmpty()) {
            Log.d("AuthInterceptor", "Agregando Authorization header con token")
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w("AuthInterceptor", "No hay token disponible - enviando request sin autenticación")
            chain.request()
        }

        Log.d("AuthInterceptor", "Headers de la petición: ${request.headers}")

        return chain.proceed(request)
    }
}