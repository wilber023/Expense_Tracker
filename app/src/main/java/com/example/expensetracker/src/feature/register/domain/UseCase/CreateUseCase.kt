package com.example.expensetracker.src.register.domain.useCase

import com.example.expensetracker.src.feature.register.domain.repository.RegisterRepository

class CreateUserUseCase(private val repository: RegisterRepository) {
    suspend operator fun invoke(username: String, pin: String): Boolean {
        return repository.registerUser(username, pin)
    }
}