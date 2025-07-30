package com.example.expensetracker.src.feature.admin.domain.Models

import com.example.expensetracker.src.feature.admin.domain.model.User

sealed class AdminEvent {
    object LoadUsers : AdminEvent()
    object RefreshData : AdminEvent()
    data class DeleteUser(val user: User) : AdminEvent()
    data class SendNotification(val user: User?, val message: String) : AdminEvent()
    data class UpdateUserStatus(val userId: String, val isActive: Boolean) : AdminEvent()
    object ClearError : AdminEvent()
    object ClearNotificationResult : AdminEvent()
    object Logout : AdminEvent()
}