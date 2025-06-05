package com.example.expensetracker.src.login.di

import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.login.data.dataSource.remote.LoginFetch
import com.example.expensetracker.src.login.data.repository.LoginRepositoryImpl
import com.example.expensetracker.src.login.domain.UseCase.ValidationUser
import com.example.expensetracker.src.login.presentation.LoginViewModel

object LoginDependencies {

    private fun provideLoginFetch(): LoginFetch {
        return LoginFetch(NetworkModule.loginApi)
    }

    private fun provideLoginRepository(): LoginRepositoryImpl {
        return LoginRepositoryImpl(provideLoginFetch())
    }

    private fun provideValidationUser(): ValidationUser {
        return ValidationUser(provideLoginRepository())
    }

    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(provideValidationUser())
    }
}
