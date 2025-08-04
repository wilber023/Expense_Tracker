package com.example.expensetracker.src.feature.admin.data.repository

import android.util.Log
import com.example.expensetracker.src.core.common.Result
import com.example.expensetracker.src.core.token.TokenRepository
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.AdminApi
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.NotificationApi
import com.example.expensetracker.src.feature.admin.data.dataSource.remote.StatusUpdateRequest
import com.example.expensetracker.src.feature.admin.domain.Models.DashboardStats
import com.example.expensetracker.src.feature.admin.domain.Models.NotificationRequest
import com.example.expensetracker.src.feature.admin.domain.Models.SendNotificationToUserRequest
import com.example.expensetracker.src.feature.admin.domain.model.User
import com.example.expensetracker.src.feature.admin.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AdminRepositoryImpl(
    private val api: AdminApi,
    private val notificationApi: NotificationApi,
    private val tokenRepository: TokenRepository
) : AdminRepository {

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val token = tokenRepository.getToken()
            if (token.isNullOrEmpty()) {
                Log.e("AdminRepository", "Token no disponible")
                return Result.Error("Token no disponible")
            }

            Log.d("AdminRepository", "Obteniendo usuarios con token")
            val response = api.getUsers("Bearer $token")
            Log.d("AdminRepository", "Usuarios obtenidos: ${response.data.size}")
            Result.Success(response.data)
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error al obtener usuarios: ${e.message}")
            Result.Error(e.message ?: "Error al obtener usuarios")
        }
    }

    override suspend fun refreshUsers(): Result<List<User>> = getAllUsers()

    override suspend fun deleteUser(user: User): Result<Unit> {
        return try {
            val token = tokenRepository.getToken()
            if (token.isNullOrEmpty()) {
                return Result.Error("Token no disponible")
            }

            Log.d("AdminRepository", "Eliminando usuario: ${user.username}")
            api.deleteUser(user.id, "Bearer $token")
            Log.d("AdminRepository", "Usuario eliminado exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error al eliminar usuario: ${e.message}")
            Result.Error(e.message ?: "Error al eliminar usuario")
        }
    }

    override suspend fun sendNotification(user: User?, message: String): Result<String> {
        return try {
            val token = tokenRepository.getToken()
            if (token.isNullOrEmpty()) {
                return Result.Error("Token no disponible")
            }

            Log.d("AdminRepository", "Enviando notificación")

            val response = if (user != null) {

                Log.d("AdminRepository", "Enviando a usuario específico: ${user.username}")
                val request = SendNotificationToUserRequest(
                    title = "Notificación del Administrador",
                    body = message,
                    userId = user.id
                )
                notificationApi.sendNotificationToUser("Bearer $token", user.id, request)
            } else {

                Log.d("AdminRepository", "Enviando a todos los usuarios")
                val request = NotificationRequest(
                    title = "Notificación del Administrador",
                    body = message
                )
                Log.d("AdminRepository", "Request creado: title=${request.title}, body=${request.body}")
                notificationApi.sendNotificationToAll("Bearer $token", request)
            }

            Log.d("AdminRepository", "Respuesta HTTP código: ${response.code()}")
            Log.d("AdminRepository", "Respuesta exitosa: ${response.isSuccessful}")
            Log.d("AdminRepository", "Body de respuesta: ${response.body()}")


            if (response.isSuccessful && response.body()?.success == true) {
                Log.d("AdminRepository", "Notificación enviada exitosamente")
                Result.Success("Notificación enviada exitosamente")
            } else {

                val errorMessage = if (response.code() == 404) {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody?.contains("No hay usuarios con tokens") == true) {
                        "No hay usuarios registrados para recibir notificaciones push"
                    } else {
                        "Servicio de notificaciones no disponible"
                    }
                } else {
                    response.body()?.message ?: "Error desconocido"
                }

                Log.e("AdminRepository", "Error en respuesta: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error al enviar notificación: ${e.message}")
            Result.Error(e.message ?: "Error al enviar notificación")
        }
    }

    override suspend fun updateUserStatus(userId: Int, isActive: Boolean): Result<Unit> {
        return try {
            val token = tokenRepository.getToken()
            if (token.isNullOrEmpty()) {
                return Result.Error("Token no disponible")
            }

            Log.d("AdminRepository", "Actualizando estado del usuario: $userId a $isActive")
            val request = StatusUpdateRequest(isActive = isActive)
            api.updateUserStatus(userId, "Bearer $token", request)
            Log.d("AdminRepository", "Estado actualizado exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error al actualizar estado: ${e.message}")
            kotlinx.coroutines.delay(500)
            Result.Success(Unit)
        }
    }

    override suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val usersResult = getAllUsers()
            when (usersResult) {
                is Result.Success -> {
                    val users = usersResult.data
                    val stats = DashboardStats(
                        totalUsers = users.size,
                        activeUsers = users.count { it.isActive },
                        inactiveUsers = users.count { !it.isActive }
                    )
                    Log.d("AdminRepository", "Estadísticas calculadas: $stats")
                    Result.Success(stats)
                }
                is Result.Error -> {
                    Log.e("AdminRepository", "Error obteniendo usuarios para stats")
                    Result.Success(DashboardStats())
                }
                else -> Result.Success(DashboardStats())
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error calculando estadísticas: ${e.message}")
            Result.Success(DashboardStats())
        }
    }

    override fun getUsersStream(): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        emit(getAllUsers())
    }
}