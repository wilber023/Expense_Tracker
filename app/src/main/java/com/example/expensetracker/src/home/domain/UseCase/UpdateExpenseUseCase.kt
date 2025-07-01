package com.example.expensetracker.src.home.domain.UseCase

import android.net.Uri
import com.example.expensetracker.src.home.data.repository.ExpenseRepositoryImpl

class UpdateExpenseUseCase(
    private val repository: ExpenseRepositoryImpl,
    private val imageProcessingUseCase: ImageProcessingUseCase
) {
    suspend operator fun invoke(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null
    ) {
        repository.updateExpense(
            id = id,
            category = category,
            description = description,
            amount = amount,
            date = date,
            imageUri = imageUri
        )
    }
}