package com.example.expensetracker.src.feature.admin.domain.usecase

import com.example.expensetracker.src.feature.admin.domain.model.User
import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository
import com.example.expensetracker.src.core.common.Result
import com.example.expensetracker.src.feature.admin.domain.Models.DashboardStats


class SendNotificationAndUpdateStatsUseCase(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(user: User?, message: String): Result<Pair<String, DashboardStats>> {
        return when (val result = repository.sendNotification(user, message)) {
            is Result.Success -> {
                when (val statsResult = repository.getDashboardStats()) {
                    is Result.Success -> {
                        Result.Success(result.data to statsResult.data)
                    }
                    is Result.Error -> {
                        // Si fallan las estadísticas, seguimos pero con vacío
                        Result.Success(result.data to DashboardStats())
                    }
                    is Result.Loading -> {
                        // Improbable, pero puedes tratarlo como DashboardStats vacío
                        Result.Success(result.data to DashboardStats())
                    }
                }
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }
}
