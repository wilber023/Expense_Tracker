package com.example.expensetracker.src.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val imageUri: String?,
    val latitude: Double?,
    val longitude: Double?,
    val locationName: String?,
    val isUploaded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)