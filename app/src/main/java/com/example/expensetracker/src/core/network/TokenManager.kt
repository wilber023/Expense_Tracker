package com.example.expensetracker.src.core.network

import android.util.Log

object TokenManager {
    private var token: String? = null

    fun setToken(token: String) {
        this.token = token
        Log.d("TokenManager", "Token guardado: $token")
        Log.d("TokenManager", "Longitud del token: ${token.length}")
        Log.d("TokenManager", "¿Es JWT válido?: ${isValidJWT(token)}")
    }

    fun getToken(): String? {
        Log.d("TokenManager", "Token solicitado: $token")
        return token
    }

    fun clearToken() {
        Log.d("TokenManager", "Token eliminado. Token anterior: $token")
        token = null
    }


    private fun isValidJWT(token: String): Boolean {
        return token.split(".").size == 3
    }
}