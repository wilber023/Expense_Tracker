package com.example.expensetracker.src.home.domain.UseCase

import com.example.expensetracker.src.home.domain.repository.ExpenseRepository

class UpdateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String
    ) {
        repository.updateExpense(id, category, description, amount, date)
    }
}