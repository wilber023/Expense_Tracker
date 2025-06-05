package com.example.expensetracker.src.home.data.dataSource.local.remote

import retrofit2.Response
import retrofit2.http.*

interface ExpenseApi {
    @POST("api/expenses")
    suspend fun addExpense(
        @Header("Authorization") authorization: String,
        @Body request: ExpenseRequest
    ): Response<ExpenseResponse>

    @GET("api/expenses")
    suspend fun getAllExpenses(
        @Header("Authorization") authorization: String
    ): Response<ExpenseListResponse>

    @PUT("api/expenses/{id}")
    suspend fun updateExpense(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body request: ExpenseRequest
    ): Response<ExpenseResponse>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<DeleteExpenseResponse>
}

data class ExpenseRequest(
    val category: String,
    val description: String,
    val amount: Double,
    val date: String
)

data class ExpenseResponse(
    val success: Boolean,
    val message: String? = null,
    val expense: ExpenseData? = null
)

data class ExpenseListResponse(
    val success: Boolean,
    val count: Int,
    val data: List<ExpenseData>
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
    val created_at: String? = null,
    val updated_at: String? = null
)