package com.example.expensetracker.src.home.domain.repository

data class Expense(
    val id: String? = null,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String
)