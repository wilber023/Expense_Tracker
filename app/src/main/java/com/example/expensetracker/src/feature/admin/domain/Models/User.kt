package com.example.expensetracker.src.feature.admin.domain.model

data class User(
    val id: String,
    val name: String,
    val document: String,
    val registeredAt: String,
    val isActive: Boolean = true
)