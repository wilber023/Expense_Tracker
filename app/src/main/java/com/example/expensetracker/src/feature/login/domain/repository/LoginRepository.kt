package com.example.expensetracker.src.feature.login.domain.repository

import com.example.expensetracker.src.feature.login.domain.model.LoginResult

interface LoginRepository {
    suspend fun validateUser(username: String, pin: String): LoginResult
    suspend fun getStoredToken(): String?
    suspend fun clearToken()
}