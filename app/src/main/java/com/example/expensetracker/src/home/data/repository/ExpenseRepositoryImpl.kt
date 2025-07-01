package com.example.expensetracker.src.home.data.repository
import com.example.expensetracker.src.core.hardware.domain.LocationData
import android.net.Uri
import com.example.expensetracker.src.home.data.dataSource.local.remote.ExpenseFetch
import com.example.expensetracker.src.home.domain.repository.Expense
import com.example.expensetracker.src.home.domain.repository.ExpenseRepository

class ExpenseRepositoryImpl(
    private val expenseFetch: ExpenseFetch
) : ExpenseRepository {
    override suspend fun addExpense(expense: Expense, imageUri: Uri?, location: LocationData?) {
        val result = expenseFetch.addExpense(
            category = expense.category,
            description = expense.description,
            amount = expense.amount,
            date = expense.date,
            imageUri = imageUri,
            location = location
        )
        result.getOrThrow()
    }
    override suspend fun deleteExpense(id: String) {
        expenseFetch.deleteExpense(id).getOrThrow()
    }
    override suspend fun getAllExpenses(): List<Expense> {
        val result = expenseFetch.getAllExpenses()
        val expenseDataList = result.getOrThrow()

        return expenseDataList.map { expenseData ->
            Expense(
                id = expenseData.id,
                category = expenseData.category,
                description = expenseData.description,
                amount = expenseData.amount.toDoubleOrNull() ?: 0.0,
                date = expenseData.date,
                imageUrl = expenseData.image_url,
                latitude = expenseData.latitude,
                longitude = expenseData.longitude,
                address = expenseData.address
            )
        }
    }
    override suspend fun updateExpense(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri?,
        location: LocationData?
    ) {
        expenseFetch.updateExpense(
            id = id,
            category = category,
            description = description,
            amount = amount,
            date = date,
            imageUri = imageUri,
            location = location
        ).getOrThrow()
    }
}