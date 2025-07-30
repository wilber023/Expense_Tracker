package com.example.expensetracker.src.feature.home.domain.UseCase

import com.example.expensetracker.src.core.offline.OfflineBackup
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository

class SyncOfflineExpensesUseCase(
    private val repository: ExpenseRepository,
    private val offlineBackup: OfflineBackup
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            val offlineExpenses = offlineBackup.getPendingExpenses()
            var successCount = 0

            for (expense in offlineExpenses) {
                try {
                    repository.addExpense(
                        expense = expense,
                        imageUri = expense.imageUrl?.let { android.net.Uri.parse(it) },
                        location = if (expense.latitude != null && expense.longitude != null) {
                            com.example.expensetracker.src.core.hardware.domain.LocationData(
                                latitude = expense.latitude,
                                longitude = expense.longitude,
                                address = expense.address
                            )
                        } else null
                    )
                    offlineBackup.markAsUploaded(expense.id!!)
                    successCount++
                } catch (_: Exception) {}
            }

            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
