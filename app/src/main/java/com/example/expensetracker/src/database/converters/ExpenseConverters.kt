package com.example.expensetracker.src.database.converters

import androidx.room.TypeConverter
import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData  // ← CAMBIAR IMPORT
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable


class ExpenseConverters {
    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun stringToUri(string: String?): Uri? = string?.let { Uri.parse(it) }

    @TypeConverter
    fun locationToJson(location: LocationData?): String? {
        return location?.let {
            Json.encodeToString(LocationDataSerial.serializer(),
                LocationDataSerial(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    address = it.address
                )
            )
        }
    }

    @TypeConverter
    fun jsonToLocation(json: String?): LocationData? {
        return json?.let {
            val serial = Json.decodeFromString(LocationDataSerial.serializer(), it)
            LocationData(
                latitude = serial.latitude,
                longitude = serial.longitude,
                address = serial.address
            )
        }
    }


    @Serializable
    private data class LocationDataSerial(
        val latitude: Double,
        val longitude: Double,
        val address: String?
    )
}