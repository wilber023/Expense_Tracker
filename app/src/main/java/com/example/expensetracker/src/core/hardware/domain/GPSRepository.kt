package com.example.expensetracker.src.core.hardware.domain


data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

interface GPSRepository {
    suspend fun getCurrentLocation(): Result<LocationData>
    fun hasLocationPermission(): Boolean
    fun isLocationEnabled(): Boolean
}