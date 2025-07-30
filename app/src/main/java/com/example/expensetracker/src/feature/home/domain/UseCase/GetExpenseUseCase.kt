package com.example.expensetracker.src.feature.home.domain.UseCase

import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import com.example.expensetracker.src.feature.home.domain.repository.Expense

class GetExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(): List<Expense> {
        return repository.getAllExpenses()
    }

    suspend fun getLocalExpenses(): List<Expense> {
        return repository.getLocalExpenses()
    }
}
