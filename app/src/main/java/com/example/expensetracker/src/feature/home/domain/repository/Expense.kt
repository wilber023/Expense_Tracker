package com.example.expensetracker.src.feature.home.domain.repository

data class Expense(
    val id: String?,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null
)