package com.example.expensetracker.src.feature.admin.domain.UseCase

import com.example.expensetracker.src.feature.admin.domain.model.User
import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository

class DeleteUserAndRefreshUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(user: User): com.example.expensetracker.src.core.common.Result<List<User>> {
        val deleteResult = repository.deleteUser(user)
        return if (deleteResult is com.example.expensetracker.src.core.common.Result.Success) {
            repository.getAllUsers()
        } else {
            deleteResult as com.example.expensetracker.src.core.common.Result.Error
        }
    }
}