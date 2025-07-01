package com.example.expensetracker.src.core.di

import android.content.Context
import com.example.expensetracker.src.core.hardware.data.FlashlightManager
import com.example.expensetracker.src.core.hardware.data.GPSManager

object HardwareModule {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    val flashlightManager: FlashlightManager by lazy {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("HardwareModule no ha sido inicializado. Llama a HardwareModule.initialize(context) primero.")
        }
        FlashlightManager(appContext)
    }

    val gpsManager: GPSManager by lazy {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("HardwareModule no ha sido inicializado. Llama a HardwareModule.initialize(context) primero.")
        }
        GPSManager(appContext)
    }
}