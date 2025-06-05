package com.example.expensetracker.src.login.data.dataSource.local.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST




interface LoginApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}