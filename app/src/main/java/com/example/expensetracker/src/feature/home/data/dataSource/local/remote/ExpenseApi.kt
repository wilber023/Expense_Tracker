package com.example.expensetracker.src.feature.home.data.dataSource.local.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ExpenseApi {
    @Multipart
    @POST("api/expenses")
    suspend fun addExpense(
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("date") date: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("address") address: RequestBody?
    ): Response<com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseResponse>

    @GET("api/expenses")
    suspend fun getAllExpenses(): Response<com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseListResponse>

    @Multipart
    @PUT("api/expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: String,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("date") date: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("address") address: RequestBody?
    ): Response<com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseResponse>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(
        @Path("id") id: String
    ): Response<com.example.expensetracker.src.feature.home.data.dataSource.local.remote.DeleteExpenseResponse>
}

data class ExpenseRequest(
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val image: String? = null
)

data class ExpenseResponse(
    val success: Boolean,
    val message: String? = null,
    val expense: com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseData? = null
)

data class ExpenseListResponse(
    val success: Boolean,
    val count: Int,
    val data: List<com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseData>
)

data class DeleteExpenseResponse(
    val success: Boolean,
    val message: String? = null
)

data class ExpenseData(
    val id: String,
    val category: String,
    val description: String,
    val amount: String,
    val date: String,
    val image_url: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)