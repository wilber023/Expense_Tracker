package com.example.expensetracker.src.core.di

import android.content.Context
import android.util.Log
import com.example.expensetracker.src.core.token.TokenRepository
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.AdminApi
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.NotificationApi
import com.example.expensetracker.src.feature.admin.data.repository.AdminRepositoryImpl
import com.example.expensetracker.src.feature.admin.data.repository.NotificationRepositoryImpl
import com.example.expensetracker.src.feature.admin.domain.UseCase.*
import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository
import com.example.expensetracker.src.feature.admin.domain.repository.NotificationRepository
import com.example.expensetracker.src.feature.admin.domain.UseCase.SendNotificationAndUpdateStatsUseCase
import com.example.expensetracker.src.feature.admin.presentation.viewModel.AdminViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {

    private var retrofit: Retrofit? = null
    private var adminApi: AdminApi? = null
    private var notificationApi: NotificationApi? = null
    private var tokenRepository: TokenRepository? = null
    private var adminRepository: AdminRepository? = null
    private var notificationRepository: NotificationRepository? = null
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

    private fun getNotificationApi(): NotificationApi {
        if (notificationApi == null) {
            Log.d("DependencyProvider", "Creando NotificationApi")
            notificationApi = getRetrofit().create(NotificationApi::class.java)
        }
        return notificationApi!!
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
                getNotificationApi(),
                getTokenRepository()
            )
        }
        return adminRepository!!
    }

    private fun getNotificationRepository(): NotificationRepository {
        if (notificationRepository == null) {
            Log.d("DependencyProvider", "Creando NotificationRepository")
            notificationRepository = NotificationRepositoryImpl(
                getRetrofit(),
                getTokenRepository()
            )
        }
        return notificationRepository!!
    }

    private fun getAdminOperationsUseCase(): AdminOperationsUseCase {
        if (adminOperationsUseCase == null) {
            Log.d("DependencyProvider", "Creando AdminOperationsUseCase")
            val repository = getAdminRepository()
            val notificationRepo = getNotificationRepository()

            adminOperationsUseCase = AdminOperationsUseCase(
                getAllUsers = GetAllUsersUseCase(repository),
                refreshUsers = RefreshUsersUseCase(repository),
                deleteUserAndRefresh = DeleteUserAndRefreshUseCase(repository),
                sendNotificationAndUpdateStats = SendNotificationAndUpdateStatsUseCase(repository),
                updateUserStatus = UpdateUserStatusUseCase(repository),
                getDashboardStats = GetDashboardStatsUseCase(repository),
                getNotifications = GetNotificationsUseCase(notificationRepo),
                markNotificationAsRead = MarkNotificationAsReadUseCase(notificationRepo)
            )
        }
        return adminOperationsUseCase!!
    }

    fun provideAdminViewModel(): AdminViewModel {
        Log.d("DependencyProvider", "Creando AdminViewModel")
        return AdminViewModel(getAdminOperationsUseCase())
    }

    fun clear() {
        retrofit = null
        adminApi = null
        notificationApi = null
        adminRepository = null
        notificationRepository = null
        adminOperationsUseCase = null
    }
}