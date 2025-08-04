package com.example.expensetracker.src.feature.login.presentation.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.FCMTokenRequest
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.NotificationApi
import com.example.expensetracker.src.feature.login.domain.UseCase.ValidationUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.expensetracker.src.core.network.TokenManager
import com.google.firebase.messaging.FirebaseMessaging

class LoginViewModel(
    private val validationUser: ValidationUser,
    private val context: Context
) : ViewModel() {

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

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _navigationHandled = MutableStateFlow(false)
    val navigationHandled: StateFlow<Boolean> = _navigationHandled


    private val notificationApi: NotificationApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://23.23.242.170/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApi::class.java)
    }

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

        Log.d("LoginViewModel", " Iniciando validación - Usuario: '$currentUsername', PIN: '${currentPin.length} dígitos'")

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
                Log.d("LoginViewModel", " Llamando al caso de uso de validación...")
                val result = validationUser(currentUsername, currentPin)

                _isLoading.value = false

                if (result.success) {
                    Log.d("LoginViewModel", " Login exitoso - Rol: ${result.userRole}")
                    _errorMessage.value = null
                    _userRole.value = result.userRole
                    _loginSuccess.value = true

                    sendFCMTokenToServer()

                } else {
                    Log.w("LoginViewModel", " Login fallido - ${result.message}")
                    _errorMessage.value = result.message ?: "Usuario o PIN incorrecto"
                    _loginSuccess.value = false
                    _userRole.value = null
                }
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("LoginViewModel", " Error durante el login: ${e.message}", e)

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

    private fun sendFCMTokenToServer() {
        viewModelScope.launch {
            try {
                val sharedPref = context.getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
                var fcmToken = sharedPref.getString("fcm_token", null)

                if (fcmToken != null) {
                    Log.d("LoginViewModel", " Enviando token FCM existente al servidor...")
                    sendTokenToServer(fcmToken)
                } else {

                    Log.d("LoginViewModel", " No hay token FCM, generando uno nuevo...")

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("LoginViewModel", " Error obteniendo token FCM", task.exception)
                            return@addOnCompleteListener
                        }

                        val newToken = task.result
                        Log.d("LoginViewModel", "Token FCM generado exitosamente")
                        Log.d("LoginViewModel", " Token: ${newToken.substring(0, 30)}...")

                        sharedPref.edit().putString("fcm_token", newToken).apply()


                        viewModelScope.launch {
                            sendTokenToServer(newToken)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", " Error en sendFCMTokenToServer: ${e.message}")
            }
        }
    }

    private suspend fun sendTokenToServer(fcmToken: String) {
        try {
            val authToken = TokenManager.getToken()
            if (!authToken.isNullOrEmpty()) {
                val request = FCMTokenRequest(push_token = fcmToken)
                val response = notificationApi.saveFCMToken("Bearer $authToken", request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("LoginViewModel", " Token FCM enviado exitosamente al servidor")
                    val sharedPref = context.getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("token_sent", true).apply()
                } else {
                    Log.e("LoginViewModel", " Error enviando token FCM: ${response.body()?.message}")
                    Log.e("LoginViewModel", " Error body: ${response.errorBody()?.string()}")
                }
            } else {
                Log.w("LoginViewModel", " No hay token de autenticación disponible")
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", " Error enviando token FCM: ${e.message}")
        }
    }

    fun markNavigationAsHandled() {
        _navigationHandled.value = true
    }
}