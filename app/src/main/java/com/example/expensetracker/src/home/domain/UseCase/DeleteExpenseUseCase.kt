package com.example.expensetracker.src.home.domain.UseCase

import com.example.expensetracker.src.home.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteExpense(id)
    }
}