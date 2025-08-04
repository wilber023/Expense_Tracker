package com.example.expensetracker.src.feature.admin.domain.repository

import com.example.expensetracker.src.feature.admin.domain.Models.Notification
import com.example.expensetracker.src.feature.admin.domain.Models.NotificationRequest
import com.example.expensetracker.src.feature.admin.domain.Models.SendNotificationToUserRequest
import com.example.expensetracker.src.core.common.Result

interface NotificationRepository {
    suspend fun sendNotificationToAll(request: NotificationRequest): Result<String>
    suspend fun sendNotificationToUser(request: SendNotificationToUserRequest): Result<String>
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markAsRead(notificationId: Int): Result<String>
    suspend fun saveFCMToken(token: String): Result<String>
}