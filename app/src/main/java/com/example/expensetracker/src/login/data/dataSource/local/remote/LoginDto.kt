package com.example.expensetracker.src.login.data.dataSource.local.remote

data class LoginRequest(
    val username: String,
    val pin: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null
)