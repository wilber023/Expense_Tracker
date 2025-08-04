package com.example.expensetracker.src.feature.admin.domain.Models



data class Notification(
    val id: Int,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null,
    val sentBy: Int?,
    val sentToUser: Int?,
    val sentToAll: Boolean = false,
    val createdAt: String,
    val isRead: Boolean = false
)

data class NotificationRequest(
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
)

data class SendNotificationToUserRequest(
    val title: String,
    val body: String,
    val data: Map<String, String>? = null,
    val userId: Int
)