package com.example.expensetracker.src.feature.home.domain.UseCase

import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.core.hardware.domain.GPSRepository

class GetLocationUseCase(
    private val gpsRepository: GPSRepository
) {
    suspend operator fun invoke(): Result<LocationData> {
        return gpsRepository.getCurrentLocation()
    }

    fun hasPermission(): Boolean {
        return gpsRepository.hasLocationPermission()
    }

    fun isLocationEnabled(): Boolean {
        return gpsRepository.isLocationEnabled()
    }
}