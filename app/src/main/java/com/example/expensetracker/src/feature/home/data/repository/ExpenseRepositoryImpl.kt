package com.example.expensetracker.src.feature.home.data.repository

import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.database.dao.ExpenseDao
import com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseFetch
import com.example.expensetracker.src.feature.home.data.mapper.ExpenseMapper
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import com.example.expensetracker.src.feature.home.domain.repository.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl(
    private val expenseFetch: ExpenseFetch,
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense, imageUri: Uri?, location: LocationData?) {
        val result = expenseFetch.addExpense(
            category = expense.category,
            description = expense.description,
            amount = expense.amount,
            date = expense.date,
            imageUri = imageUri,
            location = location
        )

        if (result.isFailure) {
            throw result.exceptionOrNull() ?: Exception("Error desconocido al agregar gasto")
        }
    }

    override fun getAllExpensesFlow(): Flow<List<Expense>> {
         return expenseDao.getAllExpenses().map { list ->
             list.map { ExpenseMapper.toDomain(it) }
         }
    }

    override suspend fun getAllExpenses(): List<Expense> {
        val result = expenseFetch.getAllExpenses()

        return if (result.isSuccess) {
            result.getOrNull()?.map { expenseData ->

                Expense(
                    id = expenseData.id,
                    category = expenseData.category,
                    description = expenseData.description,
                    amount = expenseData.amount.toDoubleOrNull() ?: 0.0,
                    date = expenseData.date,
                    imageUrl = expenseData.image_url,
                    latitude = expenseData.latitude,
                    longitude = expenseData.longitude,
                    address = expenseData.address
                )
            } ?: emptyList()
        } else {
            throw result.exceptionOrNull() ?: Exception("Error desconocido al obtener gastos")
        }
    }

    override suspend fun updateExpense(
        id: String,
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri?,
        location: LocationData?
    ) {
        val result = expenseFetch.updateExpense(
            id = id,
            category = category,
            description = description,
            amount = amount,
            date = date,
            imageUri = imageUri,
            location = location
        )

        if (result.isFailure) {
            throw result.exceptionOrNull() ?: Exception("Error desconocido al actualizar gasto")
        }
    }

    override suspend fun deleteExpense(id: String) {
        val result = expenseFetch.deleteExpense(id)

        if (result.isFailure) {
            throw result.exceptionOrNull() ?: Exception("Error desconocido al eliminar gasto")
        }
    }
}