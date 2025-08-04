package com.example.expensetracker.src.feature.login.di

import android.content.Context
import android.util.Log
import com.example.expensetracker.src.core.dataStore.DataStoreToken
import com.example.expensetracker.src.core.network.NetworkModule
import com.example.expensetracker.src.login.data.dataSource.remote.LoginFetch
import com.example.expensetracker.src.feature.login.data.repository.LoginRepositoryImpl
import com.example.expensetracker.src.feature.login.domain.UseCase.ValidationUser
import com.example.expensetracker.src.feature.login.presentation.ViewModel.LoginViewModel

object LoginDependencies {

    private fun provideLoginFetch(): LoginFetch {
        return LoginFetch(NetworkModule.loginApi)
    }

    private fun provideDataStoreToken(context: Context?): DataStoreToken? {
        return if (context != null) {
            try {
                DataStoreToken.getInstance(context)
            } catch (e: Exception) {
                Log.w("LoginDependencies", "⚠️ No se pudo crear DataStoreToken: ${e.message}")
                null
            }
        } else {
            Log.d("LoginDependencies", "📱 Contexto no disponible para DataStore")
            null
        }
    }

    private fun provideLoginRepository(context: Context?): LoginRepositoryImpl {
        val dataStore = provideDataStoreToken(context)
        Log.d("LoginDependencies", "💾 DataStore ${if (dataStore != null) "disponible" else "NO disponible"}")

        return LoginRepositoryImpl(
            provideLoginFetch(),
            context
        )
    }

    private fun provideValidationUser(context: Context?): ValidationUser {
        return ValidationUser(provideLoginRepository(context))
    }

    fun provideLoginViewModel(context: Context): LoginViewModel {
        Log.d("LoginDependencies", "🏗️ Creando ViewModel CON contexto")
        return LoginViewModel(
            provideValidationUser(context),
            context
        )
    }
}