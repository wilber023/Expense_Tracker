package com.example.expensetracker.src.register.data.dataSource.remote

data class RegisterRequest(
    val username: String,
    val pin: String
)


data class RegisterResponse(
    val success: Boolean,
    val message: String? = null,
    val user: UserData? = null
)

data class UserData(
    val id: String? = null,
    val username: String? = null,
    val created_at: String? = null
)
