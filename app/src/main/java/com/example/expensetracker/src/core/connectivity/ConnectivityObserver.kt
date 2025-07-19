package com.example.expensetracker.src.core.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>
    fun isConnected(): Boolean

   enum class Status {

       Available,
       Losing,
       Lost,
       Unavailable,
       Validated,

    }
}