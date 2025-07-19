package com.example.expensetracker.src.feature.home.domain.UseCase

import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import com.example.expensetracker.src.feature.home.domain.repository.Expense
import kotlinx.coroutines.flow.Flow

class GetExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(): List<Expense> {
        return repository.getAllExpenses()
    }

    // Nuevo método para observar los gastos en tiempo real
    fun getAllExpensesFlow(): Flow<List<Expense>> {
        return repository.getAllExpensesFlow()
    }
}