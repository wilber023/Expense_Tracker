package com.example.expensetracker.src.feature.home.domain.repository

import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense, imageUri: Uri? = null, location: LocationData? = null)
    suspend fun getAllExpenses(): List<Expense>
    fun getAllExpensesFlow(): Flow<List<Expense>>

    suspend fun updateExpense(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null,
        location: LocationData? = null
    )
    suspend fun deleteExpense(id: String)
}