package com.example.expensetracker.src.feature.admin.domain.UseCase

import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository

class UpdateUserStatusUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(userId: Int, isActive: Boolean) =
        repository.updateUserStatus(userId, isActive)
}