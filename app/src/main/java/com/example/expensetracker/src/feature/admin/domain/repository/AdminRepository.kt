package com.example.expensetracker.src.feature.admin.domain.repository

import com.example.expensetracker.src.core.common.Result
import com.example.expensetracker.src.feature.admin.domain.Models.DashboardStats
import com.example.expensetracker.src.feature.admin.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun refreshUsers(): Result<List<User>>
    suspend fun deleteUser(user: User): Result<Unit>
    suspend fun sendNotification(user: User?, message: String): Result<String>
    suspend fun updateUserStatus(userId: Int, isActive: Boolean): Result<Unit>
    suspend fun getDashboardStats(): Result<DashboardStats>
    fun getUsersStream(): Flow<Result<List<User>>>
}