package com.example.expensetracker.src.feature.admin.data.repository

import android.util.Log
import com.example.expensetracker.src.feature.admin.domain.repository.NotificationRepository
import com.example.expensetracker.src.feature.admin.domain.Models.Notification
import com.example.expensetracker.src.feature.admin.domain.Models.NotificationRequest
import com.example.expensetracker.src.feature.admin.domain.Models.SendNotificationToUserRequest
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.NotificationApi
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.FCMTokenRequest
import com.example.expensetracker.src.core.common.Result
import com.example.expensetracker.src.core.token.TokenRepository
import retrofit2.Retrofit
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val retrofit: Retrofit,
    private val tokenRepository: TokenRepository
) : NotificationRepository {

    companion object {
        private const val TAG = "NotificationRepository"
    }

    private val api by lazy {
        retrofit.create(NotificationApi::class.java)
    }

    override suspend fun sendNotificationToAll(request: NotificationRequest): Result<String> {
        return try {
            val token = tokenRepository.getToken()
            if (token == null) {
                Log.e(TAG, " Token no disponible")
                return Result.Error("Token no disponible")
            }

            Log.d(TAG, " Enviando notificación a todos: ${request.title}")

            val response = api.sendNotificationToAll("Bearer $token", request)

            Log.d(TAG, " Respuesta del servidor: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, " Notificación enviada exitosamente")
                    Result.Success(body.message)
                } else {
                    Log.e(TAG, "❌ Error en respuesta: ${body?.message}")
                    Result.Error(body?.message ?: "Error desconocido")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, " Error HTTP ${response.code()}: $errorBody")
                Result.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Excepción enviando notificación: ${e.message}", e)
            Result.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun sendNotificationToUser(request: SendNotificationToUserRequest): Result<String> {
        return try {
            val token = tokenRepository.getToken()
            if (token == null) {
                Log.e(TAG, " Token no disponible")
                return Result.Error("Token no disponible")
            }

            Log.d(TAG, " Enviando notificación a usuario ${request.userId}: ${request.title}")

            val response = api.sendNotificationToUser("Bearer $token", request.userId, request)

            Log.d(TAG, " Respuesta del servidor: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, " Notificación enviada exitosamente al usuario")
                    Result.Success(body.message)
                } else {
                    Log.e(TAG, " Error en respuesta: ${body?.message}")
                    Result.Error(body?.message ?: "Error desconocido")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, " Error HTTP ${response.code()}: $errorBody")
                Result.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Excepción enviando notificación: ${e.message}", e)
            Result.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val token = tokenRepository.getToken()
            if (token == null) {
                return Result.Error("Token no disponible")
            }

            Log.d(TAG, " Obteniendo historial de notificaciones")

            val response = api.getNotifications("Bearer $token")

            if (response.isSuccessful) {
                val notifications = response.body() ?: emptyList()
                Log.d(TAG, " Notificaciones obtenidas: ${notifications.size}")

                val domainNotifications = notifications.mapIndexed { index, dto ->
                    Notification(
                        id = index,
                        title = "Notificación",
                        body = dto.message,
                        data = null,
                        sentBy = null,
                        sentToUser = null,
                        sentToAll = dto.success,
                        createdAt = System.currentTimeMillis().toString(),
                        isRead = false
                    )
                }

                Result.Success(domainNotifications)
            } else {
                Log.e(TAG, "Error obteniendo notificaciones: ${response.message()}")
                Result.Error("Error obteniendo notificaciones")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Error obteniendo notificaciones: ${e.message}", e)
            Result.Error("Error: ${e.message}")
        }
    }

    override suspend fun markAsRead(notificationId: Int): Result<String> {
        return try {
            val token = tokenRepository.getToken()
            if (token == null) {
                return Result.Error("Token no disponible")
            }

            Log.d(TAG, " Marcando notificación como leída: $notificationId")

            val response = api.markAsRead("Bearer $token", notificationId)

            if (response.isSuccessful && response.body()?.success == true) {
                Log.d(TAG, " Notificación marcada como leída")
                Result.Success("Marcado como leído")
            } else {
                Log.e(TAG, " Error marcando como leída")
                Result.Error("Error marcando como leída")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Error marcando como leída: ${e.message}", e)
            Result.Error("Error: ${e.message}")
        }
    }

    override suspend fun saveFCMToken(token: String): Result<String> {
        return try {
            val authToken = tokenRepository.getToken()
            if (authToken == null) {
                Log.e(TAG, " Token de autenticación no disponible")
                return Result.Error("Token de autenticación no disponible")
            }

            Log.d(TAG, " Guardando token FCM en el servidor")

            val request = FCMTokenRequest(push_token = token)
            val response = api.saveFCMToken("Bearer $authToken", request)

            if (response.isSuccessful && response.body()?.success == true) {
                Log.d(TAG, " Token FCM guardado en el servidor")
                Result.Success("Token FCM guardado")
            } else {
                Log.e(TAG, " Error guardando token FCM: ${response.message()}")
                Result.Error("Error guardando token FCM")
            }
        } catch (e: Exception) {
            Log.e(TAG, " Excepción guardando token FCM: ${e.message}", e)
            Result.Error("Error: ${e.message}")
        }
    }
}