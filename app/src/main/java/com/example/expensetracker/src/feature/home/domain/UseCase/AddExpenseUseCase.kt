package com.example.expensetracker.src.feature.home.domain.UseCase

import android.content.Context
import android.net.Uri
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.core.offline.OfflineBackup
import com.example.expensetracker.src.core.utils.saveImageLocally
import com.example.expensetracker.src.feature.home.domain.repository.Expense
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import java.util.UUID

class AddExpenseUseCase(
    private val repository: ExpenseRepository,
    private val connectivityObserver: ConnectivityObserver?,
    private val offlineBackup: OfflineBackup?
) {
    suspend operator fun invoke(
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri?,
        location: LocationData?,
        context: Context
    ): Result<String> {
        return try {
            // Guardar imagen localmente (reutilizando tu función)
            val localImageUri = imageUri?.let { saveImageLocally(context, it) }

            // Construir objeto Expense
            val expense = Expense(
                id = UUID.randomUUID().toString(),
                category = category,
                description = description,
                amount = amount,
                date = date,
                imageUrl = localImageUri?.toString(),
                latitude = location?.latitude,
                longitude = location?.longitude,
                address = location?.address
            )


            if (connectivityObserver?.isConnected() == true) {
                repository.addExpense(expense, localImageUri, location)
            } else {
                offlineBackup?.saveOffline(expense)
            }

            Result.success("✅ Gasto guardado correctamente")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
