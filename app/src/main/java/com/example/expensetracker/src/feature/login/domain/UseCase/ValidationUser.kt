package com.example.expensetracker.src.feature.login.domain.UseCase

import com.example.expensetracker.src.feature.login.domain.repository.LoginRepository
import com.example.expensetracker.src.feature.login.domain.model.LoginResult

class ValidationUser(private val repository: LoginRepository) {
    suspend operator fun invoke(username: String, pin: String): LoginResult {
    return  repository.validateUser(username,pin)

    }
}