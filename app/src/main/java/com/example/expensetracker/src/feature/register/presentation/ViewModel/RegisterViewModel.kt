package com.example.expensetracker.src.register.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.src.register.domain.useCase.CreateUserUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var pin by mutableStateOf("")
        private set


    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var registerSuccess by mutableStateOf(false)
        private set

    fun onUsernameChange(newUsername: String) {
        username = newUsername

        if (errorMessage != null) {
            errorMessage = null
        }
    }

    fun onPinChange(newPin: String) {

        val numericPin = newPin.filter { it.isDigit() }.take(6)
        pin = numericPin


        if (errorMessage != null) {
            errorMessage = null
        }
    }

    fun register() {

        when {
            username.trim().isEmpty() -> {
                errorMessage = "Ingresa un nombre de usuario"
                return
            }
            username.trim().length < 3 -> {
                errorMessage = "El usuario debe tener al menos 3 caracteres"
                return
            }
            pin.isEmpty() -> {
                errorMessage = "Ingresa un PIN"
                return
            }
            pin.length < 4 -> {
                errorMessage = "El PIN debe tener al menos 4 dígitos"
                return
            }
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                Log.d("RegisterViewModel", " Iniciando registro...")
                val success = createUserUseCase(username.trim(), pin)

                isLoading = false

                if (success) {
                    Log.d("RegisterViewModel", "  Registro exitoso")
                    registerSuccess = true
                    errorMessage = null
                } else {
                    Log.w("RegisterViewModel", "  Registro falló")
                    errorMessage = "Error en el registro"
                    registerSuccess = false
                }
            } catch (e: Exception) {
                isLoading = false
                Log.e("RegisterViewModel", "  Error durante el registro: ${e.message}")
                errorMessage = e.message ?: "Error de conexión. Intenta más tarde."
                registerSuccess = false
            }
        }
    }




    fun resetRegisterSuccess() {
        registerSuccess = false
    }
}
