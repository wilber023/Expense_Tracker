package com.example.expensetracker.src.login.domain.repository

import com.example.expensetracker.src.login.domain.model.LoginResult

interface LoginRepository {
    suspend fun validateUser(username: String, pin: String): LoginResult}