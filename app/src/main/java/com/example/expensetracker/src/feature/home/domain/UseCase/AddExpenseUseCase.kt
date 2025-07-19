package com.example.expensetracker.src.feature.home.domain.UseCase

import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import com.example.expensetracker.src.feature.home.domain.repository.Expense
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver
import com.example.expensetracker.src.core.offline.OfflineBackup  // ← AGREGAR ESTE IMPORT

class AddExpenseUseCase(
    private val repository: ExpenseRepository,
    private val imageProcessingUseCase: ImageProcessingUseCase?,
    private val connectivityObserver: ConnectivityObserver?,
    private val offlineBackup: OfflineBackup?
) {
    suspend operator fun invoke(
        category: String,
        description: String,
        amount: Double,
        date: String,
        imageUri: Uri? = null,
        location: LocationData? = null
    ): Result<String> {
        val expense = Expense(
            id = null,
            category = category,
            description = description,
            amount = amount,
            date = date,
            imageUrl = null,
            latitude = location?.latitude,
            longitude = location?.longitude,
            address = location?.address
        )

        // Verificar conexión
        val hasInternet = connectivityObserver?.isConnected() ?: true

        return if (hasInternet) {
            try {
                // FLUJO NORMAL - tu código existente
                repository.addExpense(expense, imageUri, location)
                Result.success("✅ Gasto guardado correctamente")
            } catch (e: Exception) {
                // Si falla con internet, guardar offline
                offlineBackup?.saveExpense(expense, imageUri, location)
                Result.success("📱 Gasto guardado localmente. Se sincronizará cuando haya conexión.")
            }
        } else {
            // SIN INTERNET - guardar en SQLite
            offlineBackup?.saveExpense(expense, imageUri, location)
            Result.success("📱 Gasto guardado localmente. Se sincronizará cuando haya conexión.")
        }
    }
}