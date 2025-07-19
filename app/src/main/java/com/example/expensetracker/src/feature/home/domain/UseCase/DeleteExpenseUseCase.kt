package com.example.expensetracker.src.feature.home.domain.UseCase

import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteExpense(id)
    }
}