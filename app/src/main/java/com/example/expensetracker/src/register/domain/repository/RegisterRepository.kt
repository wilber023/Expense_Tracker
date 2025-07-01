package com.example.expensetracker.src.register.domain.repository

interface RegisterRepository {
    suspend fun registerUser(username: String, pin: String): Boolean
}