package com.example.expensetracker.src.core.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey("auth_token")
}