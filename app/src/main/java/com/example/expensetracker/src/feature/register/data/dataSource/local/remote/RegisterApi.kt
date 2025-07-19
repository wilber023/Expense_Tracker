package com.example.expensetracker.src.register.data.dataSource.remote

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body

interface RegisterApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}