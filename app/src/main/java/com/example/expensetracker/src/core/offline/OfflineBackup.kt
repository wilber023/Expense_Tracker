package com.example.expensetracker.src.core.offline


import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.database.dao.ExpenseDao
import com.example.expensetracker.src.feature.home.data.mapper.ExpenseMapper
import com.example.expensetracker.src.feature.home.domain.repository.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

import javax.inject.Singleton

@Singleton
class OfflineBackup  (
    private val expenseDao: ExpenseDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun saveExpense(expense: Expense, imageUri: Uri?, location: LocationData?) {
        scope.launch {
            val expenseWithId = expense.copy(
                id = expense.id ?: UUID.randomUUID().toString()
            )

            val entity = ExpenseMapper.toEntity(
                expense = expenseWithId,
                imageUri = imageUri,
                location = location,
                isUploaded = false
            )

            expenseDao.insertExpense(entity)
        }
    }

    suspend fun getPendingExpenses(): List<Expense> {
        return expenseDao.getNotUploadedExpenses().map {
            ExpenseMapper.toDomain(it)
        }
    }

    suspend fun markAsUploaded(expenseId: String) {
        expenseDao.markAsUploaded(expenseId)
    }
}