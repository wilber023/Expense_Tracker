package com.example.expensetracker.src.core.hardware.data


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.expensetracker.src.core.hardware.domain.GPSRepository
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

class GPSManager(private val context: Context) : GPSRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val geocoder: Geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override suspend fun getCurrentLocation(): Result<LocationData> {
        return suspendCancellableCoroutine { continuation ->
            if (!hasLocationPermission()) {
                continuation.resume(Result.failure(Exception("Permiso de ubicación requerido")))
                return@suspendCancellableCoroutine
            }

            if (!isLocationEnabled()) {
                continuation.resume(Result.failure(Exception("GPS deshabilitado")))
                return@suspendCancellableCoroutine
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000L
            ).apply {
                setMinUpdateDistanceMeters(10f)
                setMaxUpdateDelayMillis(30000L)
                setMaxUpdates(1)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        // Obtener dirección de forma asíncrona
                        val locationData = LocationData(
                            latitude = lastLocation.latitude,
                            longitude = lastLocation.longitude
                        )

                        try {
                            if (Geocoder.isPresent()) {
                                val addresses = geocoder.getFromLocation(
                                    lastLocation.latitude,
                                    lastLocation.longitude,
                                    1
                                )
                                val address = addresses?.firstOrNull()?.let { addr ->
                                    buildString {
                                        addr.thoroughfare?.let { append("$it ") }
                                        addr.subLocality?.let { append("$it, ") }
                                        addr.locality?.let { append(it) }
                                    }.trim().ifEmpty { null }
                                }

                                continuation.resume(Result.success(locationData.copy(address = address)))
                            } else {
                                continuation.resume(Result.success(locationData))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.success(locationData))
                        }
                    } else {
                        continuation.resume(Result.failure(Exception("No se pudo obtener ubicación")))
                    }

                    fusedLocationClient.removeLocationUpdates(this)
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )

                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }

            } catch (securityException: SecurityException) {
                continuation.resume(Result.failure(Exception("Permiso de ubicación denegado")))
            } catch (e: Exception) {
                continuation.resume(Result.failure(Exception("Error al obtener ubicación: ${e.message}")))
            }
        }
    }

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}