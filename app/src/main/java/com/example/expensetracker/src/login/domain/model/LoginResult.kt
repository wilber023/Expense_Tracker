package com.example.expensetracker.src.login.domain.model


data class LoginResult(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)