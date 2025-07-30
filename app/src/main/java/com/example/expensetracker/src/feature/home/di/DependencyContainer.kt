package com.example.expensetracker.src.feature.home.di

import android.content.Context
import androidx.room.Room
import com.example.expensetracker.src.core.di.HardwareModule
import com.example.expensetracker.src.database.AppDatabase
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver
import com.example.expensetracker.src.core.connectivity.NetworkConnectivityObserver
import com.example.expensetracker.src.core.offline.OfflineBackup
import com.example.expensetracker.src.core.sync.SyncService
import com.example.expensetracker.src.feature.home.data.dataSource.local.remote.ExpenseFetch
import com.example.expensetracker.src.feature.home.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.src.feature.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.ImageProcessingUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.GetLocationUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.SyncOfflineExpensesUseCase
import com.example.expensetracker.src.feature.home.presentation.viewModel.HomeViewModelFactory

object DependencyContainer {


    private var database: AppDatabase? = null
    private var connectivityObserver: ConnectivityObserver? = null
    private var offlineBackup: OfflineBackup? = null
    private var syncService: SyncService? = null

    private fun createExpenseRepository(context: Context): ExpenseRepositoryImpl {
        val expenseFetch = ExpenseFetch(context = context)
        val expenseDao = getDatabase(context).expenseDao()
        return ExpenseRepositoryImpl(expenseFetch, expenseDao)
    }

    private fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { database = it }
        }
    }

    private fun getConnectivityObserver(context: Context): ConnectivityObserver {
        return connectivityObserver ?: synchronized(this) {
            connectivityObserver ?: NetworkConnectivityObserver(context)
                .also { connectivityObserver = it }
        }
    }

    private fun getOfflineBackup(context: Context): OfflineBackup {
        return offlineBackup ?: synchronized(this) {
            offlineBackup ?: OfflineBackup(getDatabase(context).expenseDao())
                .also { offlineBackup = it }
        }
    }

     fun getSyncService(context: Context): SyncService {
        return syncService ?: synchronized(this) {
            syncService ?: SyncService(
                offlineBackup = getOfflineBackup(context),
                repository = createExpenseRepository(context),
                connectivityObserver = getConnectivityObserver(context)
            ).also {
                syncService = it
                it.startObservingSync { count ->
                 }
            }
        }
    }



    private fun createHomeViewModelFactory(context: Context): HomeViewModelFactory {
        HardwareModule.initialize(context)

        val expenseRepository = createExpenseRepository(context)

        val getLocationUseCase = GetLocationUseCase(HardwareModule.gpsManager)


        val connectivityObserver = getConnectivityObserver(context)
        val offlineBackup = getOfflineBackup(context)


        getSyncService(context)


        val addExpenseUseCase = AddExpenseUseCase(
            repository = expenseRepository,
            connectivityObserver = connectivityObserver,
            offlineBackup = offlineBackup
        )

        val getExpenseUseCase = GetExpenseUseCase(expenseRepository)
        val updateExpenseUseCase = UpdateExpenseUseCase(expenseRepository)
        val deleteExpenseUseCase = DeleteExpenseUseCase(expenseRepository)

        val syncOfflineExpensesUseCase = SyncOfflineExpensesUseCase(
            repository = expenseRepository,
            offlineBackup = offlineBackup
        )

        return HomeViewModelFactory(
            addExpenseUseCase,
            getExpenseUseCase,
            updateExpenseUseCase,
            deleteExpenseUseCase,
            getLocationUseCase,
            connectivityObserver,
            syncOfflineExpensesUseCase
        )

    }

    fun getHomeViewModelFactory(context: Context): HomeViewModelFactory {
        return createHomeViewModelFactory(context)
    }

}