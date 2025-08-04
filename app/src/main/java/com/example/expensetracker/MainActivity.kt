package com.example.expensetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.expensetracker.src.core.di.DependencyProvider
import com.example.expensetracker.src.core.navigation.NavigationWrapper
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", " Permiso de notificaciones concedido")
            initializeFCMToken()
        } else {
            Log.w("MainActivity", " Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DependencyProvider.initialize(this)

        setupFCM()

        setContent {
            NavigationWrapper()
        }
    }

    private fun setupFCM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                initializeFCMToken()
            }
        } else {
            initializeFCMToken()
        }
    }

    private fun initializeFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", " Error obteniendo token FCM", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MainActivity", "Token FCM: ${token.take(50)}...")

            val sharedPref = getSharedPreferences("FCM_PREFS", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("fcm_token", token)
                putBoolean("token_sent", false)
                apply()
            }
        }
    }
}