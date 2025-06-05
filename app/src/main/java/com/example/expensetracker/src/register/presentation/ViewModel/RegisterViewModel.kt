package com.example.expensetracker.src.register.presentation.viewModel

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

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPinChange(newPin: String) {
        pin = newPin
    }

    fun register() {
        viewModelScope.launch {
            createUserUseCase(username, pin)
        }
    }
}
