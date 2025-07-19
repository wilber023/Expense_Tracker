package com.example.expensetracker.src.feature.home.domain.UseCase

import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository

class UpdateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null,
        location: LocationData? = null
    ) {
        repository.updateExpense(
            id = id,
            category = category,
            description = description,
            amount = amount,
            date = date,
            imageUri = imageUri,
            location = location
        )
    }
}