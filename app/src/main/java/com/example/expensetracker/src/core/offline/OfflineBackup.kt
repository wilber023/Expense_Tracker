package com.example.expensetracker.src.core.offline

import android.net.Uri
import android.util.Log
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.database.dao.ExpenseDao
import com.example.expensetracker.src.database.entity.ExpenseEntity
import com.example.expensetracker.src.feature.home.data.mapper.ExpenseMapper
import com.example.expensetracker.src.feature.home.domain.repository.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Singleton

@Singleton
class OfflineBackup(
    private val expenseDao: ExpenseDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun saveOffline(expense: Expense) {
        scope.launch {
            val expenseWithId = expense.copy(
                id = expense.id ?: UUID.randomUUID().toString()
            )

            val location = expense.latitude?.let { lat ->
                expense.longitude?.let { lon ->
                    LocationData(
                        latitude = lat,
                        longitude = lon,
                        address = expense.address
                    )
                }
            }

            val entity = ExpenseMapper.toEntity(
                expense = expenseWithId,
                imageUri = expense.imageUrl?.let { Uri.parse(it) },
                location = location,
                isUploaded = false
            )

            Log.d("OfflineBackup", "📝 Guardado OFFLINE en SQLite: $entity")
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

    suspend fun getAllOfflineExpenses(): List<ExpenseEntity> {
        return expenseDao.getAllLocalExpenses()
    }
}
