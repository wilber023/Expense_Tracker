package com.example.expensetracker.src.feature.admin.data.dataSource.remote

import com.example.expensetracker.src.feature.admin.domain.model.User
import retrofit2.http.*

interface AdminApi {
    @GET("api/users")
    suspend fun getUsers(@Header("Authorization") token: String): UsersResponse

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    )

    @PUT("api/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: Int,
        @Header("Authorization") token: String,
        @Body request: StatusUpdateRequest
    )
}

data class UsersResponse(
    val success: Boolean,
    val count: Int,
    val data: List<User>
)


data class StatusUpdateRequest(
    val isActive: Boolean
)