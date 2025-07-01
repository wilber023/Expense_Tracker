package com.example.expensetracker.src.core.hardware.domain

interface FlashlightRepository {
    fun toggleTorch(on: Boolean)
    fun isTorchAvailable(): Boolean
}