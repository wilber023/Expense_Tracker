package com.example.expensetracker.src.core.sync

import android.util.Log
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver
import com.example.expensetracker.src.core.offline.OfflineBackup
import com.example.expensetracker.src.feature.home.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
class SyncService(
    private val offlineBackup: OfflineBackup,
    private val repository: ExpenseRepository,
    private val connectivityObserver: ConnectivityObserver
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startObservingSync(onSynced: suspend (Int) -> Unit = {}) {
        syncScope.launch {
            connectivityObserver.observe().collect { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    val syncedCount = syncPendingExpenses()
                    if (syncedCount > 0) {
                        onSynced(syncedCount)
                    }
                }
            }
        }
    }

    private suspend fun syncPendingExpenses(): Int {
        var successCount = 0
        try {
            val pendingExpenses = offlineBackup.getPendingExpenses()
            Log.d("SyncService", "Sincronizando ${pendingExpenses.size} gastos pendientes")
            for (expense in pendingExpenses) {
                try {
                    Log.d("SyncService", "Enviando gasto: $expense")
                    repository.addExpense(expense, null, null)
                    offlineBackup.markAsUploaded(expense.id!!)
                    successCount++
                } catch (_: Exception) {

                    Log.e("SyncService", "Error sincronizando ${expense.id}")

                }
            }
        } catch (_: Exception) {

        }

        return successCount
    }
}
