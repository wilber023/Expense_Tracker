package com.example.expensetracker.src.feature.admin.domain.UseCase

import com.example.expensetracker.src.feature.admin.domain.usecase.SendNotificationAndUpdateStatsUseCase

data class AdminOperationsUseCase(
    val getAllUsers: GetAllUsersUseCase,
    val refreshUsers: RefreshUsersUseCase,
    val deleteUserAndRefresh: DeleteUserAndRefreshUseCase,
    val sendNotificationAndUpdateStats: SendNotificationAndUpdateStatsUseCase,
    val updateUserStatus: UpdateUserStatusUseCase,
    val getDashboardStats: GetDashboardStatsUseCase
)