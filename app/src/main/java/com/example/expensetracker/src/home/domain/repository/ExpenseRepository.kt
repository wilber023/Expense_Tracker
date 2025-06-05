package com.example.expensetracker.src.home.domain.repository

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun getAllExpenses(): List<Expense>
    suspend fun updateExpense(id: String, category: String, description: String, amount: Double, date: String)
    suspend fun deleteExpense(id: String)
}