package com.example.expensetracker.src.feature.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.src.core.connectivity.ConnectivityObserver
import com.example.expensetracker.src.feature.home.domain.UseCase.*

class HomeViewModelFactory(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val syncOfflineExpensesUseCase: SyncOfflineExpensesUseCase
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
                connectivityObserver,
                syncOfflineExpensesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
