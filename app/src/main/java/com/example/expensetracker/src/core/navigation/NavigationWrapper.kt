package com.example.expensetracker.src.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.src.feature.home.presentation.view.HomeScreen

import com.example.expensetracker.src.feature.login.presentation.LoginScreen
import com.example.expensetracker.src.feature.register.presentation.view.RegisterScreen
import com.example.expensetracker.src.feature.register.di.DependencyContainerRegister

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = login) {
        composable<login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(home) {
                        popUpTo(login) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(register)
                }
            )
        }

        composable<home> {
            HomeScreen()
        }

        composable<register> {
            RegisterScreen(
                viewModel = DependencyContainerRegister.registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(login) {
                        popUpTo(register) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

