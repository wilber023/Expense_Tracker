package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.src.core.di.DependencyProvider
import com.example.expensetracker.src.core.navigation.NavigationWrapper
import com.example.expensetracker.src.feature.admin.presentation.view.HomeAdminScreen

import com.example.expensetracker.src.feature.login.presentation.LoginScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DependencyProvider.initialize(this)
        setContent {
//HomeAdminScreen()
            NavigationWrapper()
        }
    }
}

