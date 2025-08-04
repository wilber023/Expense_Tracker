package com.example.expensetracker.src.feature.admin.data.dataSource.remote

import com.example.expensetracker.src.feature.admin.domain.Models.NotificationRequest
import com.example.expensetracker.src.feature.admin.domain.Models.SendNotificationToUserRequest
import retrofit2.Response
import retrofit2.http.*

data class NotificationResponseDto(
    val success: Boolean,
    val message: String,
    val notificationId: Int? = null
)


data class FCMTokenRequest(
    val push_token: String
)

interface NotificationApi {
    @POST("api/notifications/send-all")
    suspend fun sendNotificationToAll(
        @Header("Authorization") token: String,
        @Body request: NotificationRequest
    ): Response<NotificationResponseDto>

    @POST("api/notifications/send-user/{userId}")
    suspend fun sendNotificationToUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body request: SendNotificationToUserRequest
    ): Response<NotificationResponseDto>

    @GET("api/notifications/history")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<List<NotificationResponseDto>>

    @PUT("api/notifications/read/{notificationId}")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Path("notificationId") notificationId: Int
    ): Response<NotificationResponseDto>

    @POST("api/users/fcm-token")
    suspend fun saveFCMToken(
        @Header("Authorization") token: String,
        @Body request: FCMTokenRequest
    ): Response<NotificationResponseDto>
}