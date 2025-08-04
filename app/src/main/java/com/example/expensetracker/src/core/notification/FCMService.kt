package com.example.expensetracker.src.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.expensetracker.MainActivity
import com.example.expensetracker.src.core.di.DependencyProvider
import com.example.expensetracker.src.feature.admin.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "expense_tracker_notifications"
        private const val CHANNEL_NAME = "Expense Tracker Notifications"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, " Mensaje FCM recibido de: ${remoteMessage.from}")
        Log.d(TAG, "Datos: ${remoteMessage.data}")

        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notificación - Título: ${notification.title}")
            Log.d(TAG, " Notificación - Cuerpo: ${notification.body}")

            showNotification(
                title = notification.title,
                body = notification.body,
                data = remoteMessage.data
            )
        }


        if (remoteMessage.notification == null && remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Expense Tracker"
            val body = remoteMessage.data["body"] ?: "Nueva notificación"

            Log.d(TAG, " Datos - Título: $title")
            Log.d(TAG, " Datos - Cuerpo: $body")

            showNotification(
                title = title,
                body = body,
                data = remoteMessage.data
            )
        }


        remoteMessage.data["notificationId"]?.let { notificationId ->
            markNotificationAsRead(notificationId.toIntOrNull() ?: 0)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, " Nuevo token FCM generado")
        Log.d(TAG, "Token: ${token.take(50)}...")


        saveTokenLocally(token)

        sendTokenToServer(token)
    }

    private fun showNotification(
        title: String?,
        body: String?,
        data: Map<String, String>
    ) {
        try {

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


                data.forEach { (key, value) ->
                    putExtra(key, value)
                }

                putExtra("from_notification", true)
                putExtra("notification_data", data.toString())
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(), // ID único
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )


            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title ?: "Expense Tracker")
                .setContentText(body ?: "Nueva notificación")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVibrate(longArrayOf(0, 300, 200, 300))


            if ((body?.length ?: 0) > 50) {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title)
                )
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            createNotificationChannel(notificationManager)


            val notificationId = data["notificationId"]?.toIntOrNull()
                ?: System.currentTimeMillis().toInt()

            notificationManager.notify(notificationId, notificationBuilder.build())

            Log.d(TAG, " Notificación mostrada con ID: $notificationId")

        } catch (e: Exception) {
            Log.e(TAG, " Error mostrando notificación: ${e.message}", e)
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de la aplicación Expense Tracker"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "📱 Canal de notificación creado con importancia HIGH")
        }
    }

    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, " Enviando token FCM al servidor...")

                val notificationRepository = getNotificationRepository()

                if (notificationRepository != null) {
                    val result = notificationRepository.saveFCMToken(token)
                    when {
                        result is com.example.expensetracker.src.core.common.Result.Success -> {
                            Log.d(TAG, " Token FCM enviado exitosamente al servidor")
                        }
                        result is com.example.expensetracker.src.core.common.Result.Error -> {
                            Log.e(TAG, " Error enviando token: ${result.message}")

                            kotlinx.coroutines.delay(30000)
                            sendTokenToServer(token)
                        }
                    }
                } else {
                    Log.e(TAG, " NotificationRepository no disponible")
                }

            } catch (e: Exception) {
                Log.e(TAG, " Excepción enviando token: ${e.message}", e)
            }
        }
    }


    private fun markNotificationAsRead(notificationId: Int) {
        if (notificationId > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val notificationRepository = getNotificationRepository()
                    notificationRepository?.markAsRead(notificationId)
                    Log.d(TAG, "Notificación $notificationId marcada como leída")
                } catch (e: Exception) {
                    Log.e(TAG, " Error marcando como leída: ${e.message}")
                }
            }
        }
    }


    private fun getNotificationRepository(): NotificationRepository? {
        return try {

            null
        } catch (e: Exception) {
            Log.e(TAG, " Error obteniendo NotificationRepository: ${e.message}")
            null
        }
    }

    private fun saveTokenLocally(token: String) {
        try {

            val sharedPref = getSharedPreferences("FCM_PREFS", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("fcm_token", token)
                putLong("token_timestamp", System.currentTimeMillis())
                apply()
            }
            Log.d(TAG, "Token FCM guardado localmente")
        } catch (e: Exception) {
            Log.e(TAG, " Error guardando token localmente: ${e.message}")
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.w(TAG, " Mensajes eliminados por el servidor FCM - posible backlog")
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.d(TAG, " Mensaje enviado correctamente: $msgId")
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.e(TAG, " Error enviando mensaje $msgId: ${exception.message}")
    }
}