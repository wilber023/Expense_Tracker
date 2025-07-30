package com.example.expensetracker.src.feature.login.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.src.feature.login.domain.UseCase.ValidationUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val validationUser: ValidationUser) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _pin = MutableStateFlow("")
    val pin: StateFlow<String> = _pin

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Propiedad para el rol del usuario
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    // Propiedad para controlar la navegación
    private val _navigationHandled = MutableStateFlow(false)
    val navigationHandled: StateFlow<Boolean> = _navigationHandled

    fun setUsername(username: String) {
        _username.value = username
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    fun setPin(pin: String) {
        val numericPin = pin.filter { it.isDigit() }.take(6)
        _pin.value = numericPin
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    fun validarLogin() {
        val currentUsername = _username.value.trim()
        val currentPin = _pin.value.trim()

        Log.d("LoginViewModel", "🔐 Iniciando validación - Usuario: '$currentUsername', PIN: '${currentPin.length} dígitos'")

        when {
            currentUsername.isEmpty() -> {
                _errorMessage.value = "Ingresa tu usuario"
                _loginSuccess.value = false
                return
            }
            currentPin.isEmpty() -> {
                _errorMessage.value = "Ingresa tu PIN"
                _loginSuccess.value = false
                return
            }
            currentPin.length < 4 -> {
                _errorMessage.value = "El PIN debe tener al menos 4 dígitos"
                _loginSuccess.value = false
                return
            }
        }

        _isLoading.value = true
        _errorMessage.value = null
        _navigationHandled.value = false

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "📡 Llamando al caso de uso de validación...")
                val result = validationUser(currentUsername, currentPin)

                _isLoading.value = false

                if (result.success) {
                    Log.d("LoginViewModel", "✅ Login exitoso - Rol: ${result.userRole}")
                    _errorMessage.value = null
                    _userRole.value = result.userRole
                    _loginSuccess.value = true
                } else {
                    Log.w("LoginViewModel", "❌ Login fallido - ${result.message}")
                    _errorMessage.value = result.message ?: "Usuario o PIN incorrecto"
                    _loginSuccess.value = false
                    _userRole.value = null
                }
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("LoginViewModel", "💥 Error durante el login: ${e.message}", e)

                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "Error de conexión. Verifica tu red."
                    e.message?.contains("Connection refused") == true ->
                        "No se puede conectar al servidor."
                    e.message?.contains("timeout") == true ->
                        "Tiempo de espera agotado. Intenta de nuevo."
                    else -> "Error de conexión. Intenta más tarde."
                }

                _errorMessage.value = errorMsg
                _loginSuccess.value = false
                _userRole.value = null
            }
        }
    }

    fun markNavigationAsHandled() {
        _navigationHandled.value = true
    }

    fun resetLoginState() {
        _loginSuccess.value = false
        _userRole.value = null
        _navigationHandled.value = false
        _errorMessage.value = null
    }
}
