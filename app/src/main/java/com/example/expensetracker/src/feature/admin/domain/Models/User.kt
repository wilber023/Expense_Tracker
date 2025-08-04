package com.example.expensetracker.src.feature.admin.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val role: String,
    @SerializedName("push_token")
    val pushToken: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    val isActive: Boolean = true
)