package com.example.expensetracker.src.feature.register.domain.repository

interface RegisterRepository {
    suspend fun registerUser(username: String, pin: String): Boolean
}