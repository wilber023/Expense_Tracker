package com.example.expensetracker.src.core.di

import android.content.Context
import android.util.Log
import com.example.expensetracker.src.core.token.TokenRepository
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.AdminApi
import com.example.expensetracker.src.feature.admin.data.repository.AdminRepositoryImpl
import com.example.expensetracker.src.feature.admin.domain.UseCase.*
import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository
import com.example.expensetracker.src.feature.admin.domain.usecase.SendNotificationAndUpdateStatsUseCase
import com.example.expensetracker.src.feature.admin.presentation.viewModel.AdminViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {

    private var retrofit: Retrofit? = null
    private var adminApi: AdminApi? = null
    private var tokenRepository: TokenRepository? = null
    private var adminRepository: AdminRepository? = null
    private var adminOperationsUseCase: AdminOperationsUseCase? = null

    fun initialize(context: Context) {
        Log.d("DependencyProvider", "Inicializando dependencias")
        tokenRepository = TokenRepository.getInstance(context)
    }

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            Log.d("DependencyProvider", "Creando Retrofit")
            retrofit = Retrofit.Builder()
                .baseUrl("http://23.23.242.170/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private fun getAdminApi(): AdminApi {
        if (adminApi == null) {
            Log.d("DependencyProvider", "Creando AdminApi")
            adminApi = getRetrofit().create(AdminApi::class.java)
        }
        return adminApi!!
    }

    private fun getTokenRepository(): TokenRepository {
        if (tokenRepository == null) {
            throw IllegalStateException("DependencyProvider no ha sido inicializado. Llama a initialize() primero.")
        }
        return tokenRepository!!
    }

    private fun getAdminRepository(): AdminRepository {
        if (adminRepository == null) {
            Log.d("DependencyProvider", "Creando AdminRepository")
            adminRepository = AdminRepositoryImpl(
                getAdminApi(),
                getTokenRepository()
            )
        }
        return adminRepository!!
    }

    private fun getAdminOperationsUseCase(): AdminOperationsUseCase {
        if (adminOperationsUseCase == null) {
            Log.d("DependencyProvider", "Creando AdminOperationsUseCase")
            val repository = getAdminRepository()
            adminOperationsUseCase = AdminOperationsUseCase(
                getAllUsers = GetAllUsersUseCase(repository),
                refreshUsers = RefreshUsersUseCase(repository),
                deleteUserAndRefresh = DeleteUserAndRefreshUseCase(repository),
                sendNotificationAndUpdateStats = SendNotificationAndUpdateStatsUseCase(repository),
                updateUserStatus = UpdateUserStatusUseCase(repository),
                getDashboardStats = GetDashboardStatsUseCase(repository)
            )
        }
        return adminOperationsUseCase!!
    }

    fun provideAdminViewModel(): AdminViewModel {
        Log.d("DependencyProvider", "Creando AdminViewModel")
        return AdminViewModel(getAdminOperationsUseCase())
    }

    // Método para limpiar dependencias si es necesario
    fun clear() {
        retrofit = null
        adminApi = null
        adminRepository = null
        adminOperationsUseCase = null
        // tokenRepository se mantiene como singleton
    }
}
