package com.example.expensetracker.src.home.di

import android.content.Context
import com.example.expensetracker.src.core.di.HardwareModule
import com.example.expensetracker.src.home.data.dataSource.local.remote.ExpenseFetch
import com.example.expensetracker.src.home.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.src.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.ImageProcessingUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetLocationUseCase
import com.example.expensetracker.src.home.presentation.viewModel.HomeViewModelFactory

object DependencyContainer {

    private fun createExpenseRepository(context: Context?): ExpenseRepositoryImpl {
        val expenseFetch = ExpenseFetch(context = context)
        return ExpenseRepositoryImpl(expenseFetch)
    }

    fun getHomeViewModelFactory(context: Context): HomeViewModelFactory {
        // Inicializar HardwareModule
        HardwareModule.initialize(context)

        val expenseRepository = createExpenseRepository(context)
        val imageProcessingUseCase = ImageProcessingUseCase(context)
        val getLocationUseCase = GetLocationUseCase(HardwareModule.gpsManager)

        val addExpenseUseCase = AddExpenseUseCase(expenseRepository, imageProcessingUseCase)
        val getExpenseUseCase = GetExpenseUseCase(expenseRepository)
        val updateExpenseUseCase = UpdateExpenseUseCase(expenseRepository, imageProcessingUseCase)
        val deleteExpenseUseCase = DeleteExpenseUseCase(expenseRepository)

        return HomeViewModelFactory(
            addExpenseUseCase,
            getExpenseUseCase,
            updateExpenseUseCase,
            deleteExpenseUseCase,
            getLocationUseCase
        )
    }

    // Factory de respaldo sin funcionalidad de imágenes - solo para compatibilidad
    val homeViewModelFactory: HomeViewModelFactory by lazy {
        throw IllegalStateException(
            "No usar homeViewModelFactory directamente. " +
                    "Usar getHomeViewModelFactory(context) en su lugar para soporte completo de características."
        )
    }
}