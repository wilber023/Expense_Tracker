package com.example.expensetracker.src.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.src.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetLocationUseCase

class HomeViewModelFactory(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(
            addExpenseUseCase,
            getExpenseUseCase,
            updateExpenseUseCase,
            deleteExpenseUseCase,
            getLocationUseCase
        ) as T
    }
}