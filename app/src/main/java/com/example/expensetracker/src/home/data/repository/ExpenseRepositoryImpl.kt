package com.example.expensetracker.src.home.data.repository

import com.example.expensetracker.src.home.data.dataSource.local.remote.ExpenseFetch
import com.example.expensetracker.src.home.domain.repository.Expense
import com.example.expensetracker.src.home.domain.repository.ExpenseRepository

class ExpenseRepositoryImpl(
    private val expenseFetch: ExpenseFetch = ExpenseFetch()
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense) {
        val result = expenseFetch.addExpense(
            category = expense.category,
            description = expense.description,
            amount = expense.amount,
            date = expense.date
        )
        result.getOrThrow()
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
                date = expenseData.date
            )
        }
    }

    override suspend fun updateExpense(id: String, category: String, description: String, amount: Double, date: String) {
        expenseFetch.updateExpense(
            id = id,
            category = category,
            description = description,
            amount = amount,
            date = date
        ).getOrThrow()
    }

    override suspend fun deleteExpense(id: String) {
        expenseFetch.deleteExpense(id).getOrThrow()
    }
}