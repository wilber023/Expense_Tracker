package com.example.expensetracker.src.core.sync
import android.util.Log
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver
import com.example.expensetracker.src.core.offline.OfflineBackup
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

import javax.inject.Singleton

@Singleton
class SyncService  (
    private val offlineBackup: OfflineBackup,
    private val repository: ExpenseRepository,
    private val connectivityObserver: ConnectivityObserver
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startObservingSync() {
        syncScope.launch {
            connectivityObserver.observe().collect { status ->
                when (status) {
                    ConnectivityObserver.Status.Available -> {
                        Log.d("SyncService", "Conexión disponible, sincronizando...")
                        syncPendingExpenses()
                    }
                    else -> {
                        Log.d("SyncService", "Sin conexión")
                    }
                }
            }
        }
    }

    private suspend fun syncPendingExpenses() {
        try {
            val pendingExpenses = offlineBackup.getPendingExpenses()
            Log.d("SyncService", "Sincronizando ${pendingExpenses.size} gastos pendientes")

            pendingExpenses.forEach { expense ->
                try {

                    repository.addExpense(expense, null, null)
                    offlineBackup.markAsUploaded(expense.id!!)
                    Log.d("SyncService", "Gasto ${expense.id} sincronizado")
                } catch (e: Exception) {
                    Log.e("SyncService", "Error sincronizando ${expense.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncService", "Error en sincronización: ${e.message}")
        }
    }
}