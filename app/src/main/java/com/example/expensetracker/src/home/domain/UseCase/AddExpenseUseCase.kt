package com.example.expensetracker.src.home.domain.UseCase

import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.home.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.src.home.domain.repository.Expense

class AddExpenseUseCase(
    private val repository: ExpenseRepositoryImpl,
    private val imageProcessingUseCase: ImageProcessingUseCase
) {
    suspend operator fun invoke(
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null,
        location: LocationData? = null

    ) {
        val expense = Expense(
            category = category,
            description = description,
            amount = amount,
            date = date
        )
        repository.addExpense(expense, imageUri,location)
    }
}