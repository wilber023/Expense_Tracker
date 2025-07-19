package com.example.expensetracker.src.feature.home.data.mapper

import android.net.Uri
import com.example.expensetracker.src.core.hardware.domain.LocationData
import com.example.expensetracker.src.database.entity.ExpenseEntity
import com.example.expensetracker.src.feature.home.domain.repository.Expense

object ExpenseMapper {

    fun toEntity(
        expense: Expense,
        imageUri: Uri? = null,
        location: LocationData? = null,
        isUploaded: Boolean = false
    ): ExpenseEntity {
        return ExpenseEntity(
            id = expense.id ?: "",
            category = expense.category,
            description = expense.description,
            amount = expense.amount,
            date = expense.date,
            imageUri = imageUri?.toString(),
            latitude = location?.latitude,
            longitude = location?.longitude,
            locationName = location?.address,
            isUploaded = isUploaded,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun toDomain(entity: ExpenseEntity): Expense {
        return Expense(
            id = if (entity.id.isEmpty()) null else entity.id,
            category = entity.category,
            description = entity.description,
            amount = entity.amount,
            date = entity.date,
            imageUrl = entity.imageUri,
            latitude = entity.latitude,
            longitude = entity.longitude,
            address = entity.locationName
        )
    }

    fun toLocationData(entity: ExpenseEntity): LocationData? {
        return if (entity.latitude != null && entity.longitude != null) {
            LocationData(
                latitude = entity.latitude,
                longitude = entity.longitude,
                address = entity.locationName
            )
        } else null
    }

    fun getImageUri(entity: ExpenseEntity): Uri? {
        return entity.imageUri?.let { Uri.parse(it) }
    }
}