package com.example.expensetracker.src.feature.admin.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.src.core.common.Result
import com.example.expensetracker.src.feature.admin.domain.UseCase.AdminOperationsUseCase
import com.example.expensetracker.src.feature.admin.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val users: List<User> = emptyList(),
    val dashboardStats: Any? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val notificationMessage: String? = null
)

sealed class AdminEvent {
    object LoadUsers : AdminEvent()
    object RefreshData : AdminEvent()
    data class DeleteUser(val user: User) : AdminEvent()
    data class SendNotification(val user: User?, val message: String) : AdminEvent()
    data class UpdateUserStatus(val userId: String, val isActive: Boolean) : AdminEvent()
    object ClearError : AdminEvent()
    object ClearNotificationMessage : AdminEvent()
    object Logout : AdminEvent()
}

class AdminViewModel(
    private val adminOperations: AdminOperationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        Log.d("AdminViewModel", "ViewModel inicializado")
        loadUsers()
    }

    fun onEvent(event: AdminEvent) {
        Log.d("AdminViewModel", "Evento recibido: $event")
        when (event) {
            is AdminEvent.LoadUsers -> loadUsers()
            is AdminEvent.RefreshData -> refreshData()
            is AdminEvent.DeleteUser -> deleteUser(event.user)
            is AdminEvent.SendNotification -> sendNotification(event.user, event.message)
            is AdminEvent.UpdateUserStatus -> updateUserStatus(event.userId, event.isActive)
            is AdminEvent.ClearError -> clearError()
            is AdminEvent.ClearNotificationMessage -> clearNotificationMessage()
            is AdminEvent.Logout -> logout()
        }
    }

    private fun loadUsers() = viewModelScope.launch {
        Log.d("AdminViewModel", "Cargando usuarios...")
        _uiState.value = _uiState.value.copy(isLoading = true)

        when (val result = adminOperations.getAllUsers()) {
            is Result.Success -> {
                Log.d("AdminViewModel", "Usuarios cargados: ${result.data.size}")
                val stats = adminOperations.getDashboardStats()
                _uiState.value = _uiState.value.copy(
                    users = result.data,
                    dashboardStats = (stats as? Result.Success)?.data,
                    isLoading = false,
                    errorMessage = null
                )
            }
            is Result.Error -> {
                Log.e("AdminViewModel", "Error cargando usuarios: ${result.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
            else -> {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun refreshData() = viewModelScope.launch {
        Log.d("AdminViewModel", "Refrescando datos...")
        _uiState.value = _uiState.value.copy(isRefreshing = true)

        when (val result = adminOperations.refreshUsers()) {
            is Result.Success -> {
                Log.d("AdminViewModel", "Datos refrescados: ${result.data.size}")
                val stats = adminOperations.getDashboardStats()
                _uiState.value = _uiState.value.copy(
                    users = result.data,
                    dashboardStats = (stats as? Result.Success)?.data,
                    isRefreshing = false,
                    errorMessage = null
                )
            }
            is Result.Error -> {
                Log.e("AdminViewModel", "Error refrescando: ${result.message}")
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = result.message
                )
            }
            else -> {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    private fun deleteUser(user: User) = viewModelScope.launch {
        Log.d("AdminViewModel", "Eliminando usuario: ${user.name}")
        when (val result = adminOperations.deleteUserAndRefresh(user)) {
            is Result.Success -> {
                Log.d("AdminViewModel", "Usuario eliminado exitosamente")
                _uiState.value = _uiState.value.copy(
                    users = result.data,
                    errorMessage = null
                )
                updateDashboardStats()
            }
            is Result.Error -> {
                Log.e("AdminViewModel", "Error eliminando usuario: ${result.message}")
                _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
            else -> {}
        }
    }

    private fun sendNotification(user: User?, message: String) = viewModelScope.launch {
        Log.d("AdminViewModel", "Enviando notificación")
        when (val result = adminOperations.sendNotificationAndUpdateStats(user, message)) {
            is Result.Success -> {
                Log.d("AdminViewModel", "Notificación enviada exitosamente")
                _uiState.value = _uiState.value.copy(
                    dashboardStats = result.data.second,
                    notificationMessage = "Notificación enviada exitosamente",
                    errorMessage = null
                )
            }
            is Result.Error -> {
                Log.e("AdminViewModel", "Error enviando notificación: ${result.message}")
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
            }
            else -> {}
        }
    }

    private fun updateUserStatus(userId: String, isActive: Boolean) = viewModelScope.launch {
        Log.d("AdminViewModel", "Actualizando estado del usuario: $userId -> $isActive")
        when (val result = adminOperations.updateUserStatus(userId, isActive)) {
            is Result.Success -> {
                Log.d("AdminViewModel", "Estado actualizado exitosamente")
                val updated = _uiState.value.users.map {
                    if (it.id == userId) it.copy(isActive = isActive) else it
                }
                _uiState.value = _uiState.value.copy(
                    users = updated,
                    errorMessage = null
                )
                updateDashboardStats()
            }
            is Result.Error -> {
                Log.e("AdminViewModel", "Error actualizando estado: ${result.message}")
                _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
            else -> {}
        }
    }

    private fun updateDashboardStats() = viewModelScope.launch {
        when (val result = adminOperations.getDashboardStats()) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(dashboardStats = result.data)
            }
            else -> {}
        }
    }

    private fun clearError() {
        Log.d("AdminViewModel", "Limpiando error")
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun clearNotificationMessage() {
        Log.d("AdminViewModel", "Limpiando mensaje de notificación")
        _uiState.value = _uiState.value.copy(notificationMessage = null)
    }

    private fun logout() {
        Log.d("AdminViewModel", "Cerrando sesión")
        _uiState.value = AdminUiState()
    }
}
