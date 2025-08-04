package com.example.expensetracker.src.feature.login.data.dataSource.local.remote

data class LoginRequest(
    val role: String,
    val username: String,
    val pin: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null,
    val userRole: String? = null,
    val userId: Int? = null
)