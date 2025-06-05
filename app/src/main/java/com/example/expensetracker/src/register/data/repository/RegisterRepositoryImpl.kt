package com.example.expensetracker.src.register.data.repository

import com.example.expensetracker.src.register.data.dataSource.remote.RegisterApi
import com.example.expensetracker.src.register.data.dataSource.remote.RegisterRequest
import com.example.expensetracker.src.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val api: RegisterApi
) : RegisterRepository {
    override suspend fun registerUser(username: String, pin: String) {
        val request = RegisterRequest(username, pin)
        api.register(request)
    }
}
