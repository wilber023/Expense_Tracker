package com.example.expensetracker.src.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.expensetracker.src.feature.admin.presentation.view.HomeAdminScreen
import com.example.expensetracker.src.feature.home.presentation.view.HomeScreen
import com.example.expensetracker.src.feature.login.presentation.view.LoginScreen
import com.example.expensetracker.src.feature.login.presentation.ViewModel.LoginViewModel
import com.example.expensetracker.src.feature.login.di.LoginDependencies
import com.example.expensetracker.src.feature.register.presentation.view.RegisterScreen
import com.example.expensetracker.src.feature.register.di.DependencyContainerRegister

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = login) {
        composable<login> {
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel {
                LoginDependencies.provideLoginViewModel(context)
            }

            val loginSuccess by loginViewModel.loginSuccess.collectAsState()
            val userRole by loginViewModel.userRole.collectAsState()
            val navigationHandled by loginViewModel.navigationHandled.collectAsState()

            // 🚀 NAVEGACIÓN AUTOMÁTICA BASADA EN ROLES
            LaunchedEffect(loginSuccess, userRole) {
                if (loginSuccess && userRole != null && !navigationHandled) {
                    loginViewModel.markNavigationAsHandled()

                    // ✅ SOLUCIÓN: Variable local para evitar smart cast error
                    val currentRole = userRole
                    android.util.Log.d("Navigation", "🧭 Navegando con rol: $currentRole")

                    when (currentRole?.lowercase()) {
                        "admin" -> {
                            android.util.Log.d("Navigation", "👑 Navegando a Admin")
                            navController.navigate(homeAdmin) {
                                popUpTo(login) { inclusive = true }
                            }
                        }
                        "user" -> {
                            android.util.Log.d("Navigation", "👤 Navegando a Usuario")
                            navController.navigate(home) {
                                popUpTo(login) { inclusive = true }
                            }
                        }
                        else -> {
                            android.util.Log.d("Navigation", "🏠 Navegando a Home (default)")
                            navController.navigate(home) {
                                popUpTo(login) { inclusive = true }
                            }
                        }
                    }
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // La navegación se maneja automáticamente en LaunchedEffect
                },
                onRegisterClick = {
                    navController.navigate(register)
                }
            )
        }

        composable<home> {
            HomeScreen()
        }

        composable<homeAdmin> {
            HomeAdminScreen()
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