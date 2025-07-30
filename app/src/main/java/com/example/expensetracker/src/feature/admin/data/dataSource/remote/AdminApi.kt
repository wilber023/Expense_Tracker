package com.example.expensetracker.src.feature.admin.data.dataSource.remote

import com.example.expensetracker.src.feature.admin.domain.model.User
import retrofit2.http.*

interface AdminApi {
    @GET("api/auth/users")
    suspend fun getUsers(@Header("Authorization") token: String): List<User>

    @DELETE("api/auth/users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    )

    @POST("api/notifications/send")
    suspend fun sendNotification(
        @Header("Authorization") token: String,
        @Body request: NotificationRequest
    ): NotificationResponse

    @PUT("api/auth/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: String,
        @Header("Authorization") token: String,
        @Body request: StatusUpdateRequest
    )
}

data class NotificationRequest(
    val userId: String? = null, // null para todos los usuarios
    val message: String
)

data class NotificationResponse(
    val success: Boolean,
    val message: String
)

data class StatusUpdateRequest(
    val isActive: Boolean
)