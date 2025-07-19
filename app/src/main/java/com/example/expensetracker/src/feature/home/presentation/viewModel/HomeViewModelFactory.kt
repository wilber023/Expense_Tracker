// Actualizar tu HomeViewModelFactory existente
package com.example.expensetracker.src.feature.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.src.feature.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.feature.home.domain.UseCase.GetLocationUseCase
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver

class HomeViewModelFactory(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                addExpenseUseCase,
                getExpenseUseCase,
                updateExpenseUseCase,
                deleteExpenseUseCase,
                getLocationUseCase,
                connectivityObserver // NUEVO PARÁMETRO
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}