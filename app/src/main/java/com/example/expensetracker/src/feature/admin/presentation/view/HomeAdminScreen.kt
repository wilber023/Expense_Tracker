package com.example.expensetracker.src.feature.admin.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.src.core.di.DependencyProvider
import com.example.expensetracker.src.feature.admin.domain.model.User
import com.example.expensetracker.src.feature.admin.presentation.viewModel.AdminViewModel
import com.example.expensetracker.src.feature.admin.presentation.viewModel.AdminEvent
import com.example.expensetracker.src.feature.admin.presentation.theme.ExecutiveTheme
import com.example.expensetracker.src.feature.admin.presentation.theme.NotificationType
import com.example.expensetracker.src.feature.admin.presentation.theme.getDashboardCards
import com.example.expensetracker.src.feature.admin.presentation.components.*

@Composable
fun HomeAdminScreen(
    viewModel: AdminViewModel? = null,
    onNavigateToUserDetail: (User) -> Unit = {}
) {
    val actualViewModel = viewModel ?: viewModel<AdminViewModel> {
        DependencyProvider.provideAdminViewModel()
    }
    val uiState by actualViewModel.uiState.collectAsStateWithLifecycle()
    var showNotificationDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    // Efectos secundarios
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            kotlinx.coroutines.delay(4000)
            actualViewModel.onEvent(AdminEvent.ClearError)
        }
    }

    LaunchedEffect(uiState.notificationMessage) {
        if (uiState.notificationMessage != null) {
            kotlinx.coroutines.delay(3000)
            actualViewModel.onEvent(AdminEvent.ClearNotificationMessage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ExecutiveTheme.DeepNavy)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header ejecutivo mejorado
            ExecutiveHeader(
                onLogout = { actualViewModel.onEvent(AdminEvent.Logout) }
            )

            // Contenido principal con scroll
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Indicador de carga
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = ExecutiveTheme.PremiumGold,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando información...",
                                    color = ExecutiveTheme.LightGray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }


                item {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))


                        Text(
                            text = "PANEL DE CONTROL",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = ExecutiveTheme.PureWhite,
                                letterSpacing = 1.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            items(
                                items = getDashboardCards(uiState.users.size, uiState.dashboardStats),
                                key = { it.label }
                            ) { card ->
                                PremiumDashboardCard(card = card)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Sección de usuarios con header ejecutivo
                item {
                    UsersSectionHeader(
                        userCount = uiState.users.size,
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { actualViewModel.onEvent(AdminEvent.RefreshData) },
                        onNotifyAll = {
                            selectedUser = null
                            showNotificationDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Lista de usuarios o estado vacío
                if (uiState.users.isEmpty() && !uiState.isLoading) {
                    item {
                        ExecutiveEmptyState()
                    }
                } else {
                    items(
                        items = uiState.users,
                        key = { it.id }
                    ) { user ->
                        ExecutiveUserCard(
                            user = user,
                            onView = { onNavigateToUserDetail(user) },
                            onDelete = {
                                actualViewModel.onEvent(AdminEvent.DeleteUser(user))
                            },
                            onNotify = {
                                selectedUser = user
                                showNotificationDialog = true
                            },
                            onToggleStatus = { isActive ->
                                actualViewModel.onEvent(AdminEvent.UpdateUserStatus(user.id, isActive))
                            },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Footer ejecutivo
        ExecutiveFooter(
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Mensajes de notificación mejorados
        uiState.errorMessage?.let { error ->
            ExecutiveNotificationCard(
                message = error,
                type = NotificationType.Error,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(24.dp)
            )
        }

        uiState.notificationMessage?.let { message ->
            ExecutiveNotificationCard(
                message = message,
                type = NotificationType.Success,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(24.dp)
            )
        }
    }

    // Dialog de notificación ejecutivo
    if (showNotificationDialog) {
        ExecutiveNotificationDialog(
            user = selectedUser,
            onDismiss = { showNotificationDialog = false },
            onSend = { message ->
                actualViewModel.onEvent(AdminEvent.SendNotification(selectedUser, message))
                showNotificationDialog = false
            }
        )
    }
}