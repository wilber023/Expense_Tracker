package com.example.expensetracker.src.feature.register.di

import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.feature.register.data.repository.RegisterRepositoryImpl
import com.example.expensetracker.src.register.domain.useCase.CreateUserUseCase
import com.example.expensetracker.src.register.presentation.viewModel.RegisterViewModel

object DependencyContainerRegister {

    private val api = NetworkModule.registerApi
    private val repository = RegisterRepositoryImpl(api)
    private val createUserUseCase = CreateUserUseCase(repository)
    val registerViewModel = RegisterViewModel(createUserUseCase)
}
