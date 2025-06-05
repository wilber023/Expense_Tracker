package com.example.expensetracker.src.home.di

import com.example.expensetracker.src.home.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.src.home.domain.UseCase.AddExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.GetExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.UpdateExpenseUseCase
import com.example.expensetracker.src.home.domain.UseCase.DeleteExpenseUseCase
import com.example.expensetracker.src.home.presentation.viewModel.HomeViewModelFactory

object DependencyContainer {

    private val expenseRepository: ExpenseRepositoryImpl = ExpenseRepositoryImpl()

    private val addExpenseUseCase: AddExpenseUseCase = AddExpenseUseCase(expenseRepository)
    private val getExpenseUseCase: GetExpenseUseCase = GetExpenseUseCase(expenseRepository)
    private val updateExpenseUseCase: UpdateExpenseUseCase = UpdateExpenseUseCase(expenseRepository)
    private val deleteExpenseUseCase: DeleteExpenseUseCase = DeleteExpenseUseCase(expenseRepository)

    val homeViewModelFactory: HomeViewModelFactory = HomeViewModelFactory(
        addExpenseUseCase,
        getExpenseUseCase,
        updateExpenseUseCase,
        deleteExpenseUseCase
    )
}