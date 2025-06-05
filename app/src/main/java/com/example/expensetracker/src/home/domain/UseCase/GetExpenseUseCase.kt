package com.example.expensetracker.src.home.domain.UseCase

import com.example.expensetracker.src.home.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.src.home.domain.repository.Expense

class GetExpenseUseCase(
    private val repository: ExpenseRepositoryImpl
) {
    suspend operator fun invoke(): List<Expense> {
        return repository.getAllExpenses()
    }
}
