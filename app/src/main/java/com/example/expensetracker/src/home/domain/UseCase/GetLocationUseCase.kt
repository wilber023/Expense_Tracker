package com.example.expensetracker.src.home.domain.UseCase


import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.core.hardware.data.GPSManager

class GetLocationUseCase(
    private val gpsManager: GPSManager
) {
    suspend operator fun invoke(): Result<LocationData> {
        return gpsManager.getCurrentLocation()
    }

    fun hasPermission(): Boolean {
        return gpsManager.hasLocationPermission()
    }

    fun isGPSEnabled(): Boolean {
        return gpsManager.isLocationEnabled()
    }
}