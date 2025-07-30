package com.example.expensetracker.src.feature.admin.domain.UseCase

import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository

class RefreshUsersUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.refreshUsers()
}