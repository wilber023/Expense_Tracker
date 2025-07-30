package com.example.expensetracker.src.feature.login.domain.model

data class LoginResult(
    val success: Boolean,
    val message: String? = null,
    val userRole: String? = null,
    val userId: Int? = null,
    val token: String? = null
)