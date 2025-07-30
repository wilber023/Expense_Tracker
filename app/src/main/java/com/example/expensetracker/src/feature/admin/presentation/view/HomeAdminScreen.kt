package com.example.expensetracker.src.feature.admin.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.src.core.di.DependencyProvider
import com.example.expensetracker.src.feature.admin.domain.model.User
import com.example.expensetracker.src.feature.admin.presentation.viewModel.AdminViewModel
import com.example.expensetracker.src.feature.admin.presentation.viewModel.AdminEvent

// Paleta de colores ejecutiva y futurista
object ExecutiveTheme {
    // Colores principales
    val DeepNavy = Color(0xFF0A0E1A)
    val RichBlack = Color(0xFF0D1117)
    val EliteBlue = Color(0xFF1E3A8A)
    val PremiumGold = Color(0xFFD4AF37)
    val PlatinumSilver = Color(0xFFE5E7EB)
    val ExecutiveGray = Color(0xFF374151)
    val LightGray = Color(0xFF9CA3AF)
    val PureWhite = Color(0xFFFFFFFF)

    // Colores de estado
    val SuccessGreen = Color(0xFF10B981)
    val DangerRed = Color(0xFFEF4444)
    val WarningAmber = Color(0xFFF59E0B)
    val InfoBlue = Color(0xFF3B82F6)

    // Gradientes sofisticados
    val PrimaryGradient = Brush.linearGradient(
        colors = listOf(DeepNavy, EliteBlue.copy(alpha = 0.9f))
    )

    val CardGradient = Brush.linearGradient(
        colors = listOf(
            RichBlack,
            ExecutiveGray.copy(alpha = 0.8f)
        )
    )

    val AccentGradient = Brush.linearGradient(
        colors = listOf(
            PremiumGold.copy(alpha = 0.1f),
            EliteBlue.copy(alpha = 0.05f)
        )
    )

    val GlassEffect = Brush.linearGradient(
        colors = listOf(
            PureWhite.copy(alpha = 0.1f),
            PureWhite.copy(alpha = 0.05f)
        )
    )
}

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
            // Header ejecutivo
            ExecutiveHeader(
                onLogout = { actualViewModel.onEvent(AdminEvent.Logout) }
            )

            // Indicador de carga
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ExecutiveTheme.PremiumGold,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Dashboard Cards Premium
            LazyRow(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                items(
                    items = getDashboardCards(uiState.users.size, uiState.dashboardStats),
                    key = { it.label }
                ) { card ->
                    PremiumDashboardCard(card = card)
                }
            }

            // Sección de usuarios con header ejecutivo
            UsersSectionHeader(
                userCount = uiState.users.size,
                isRefreshing = uiState.isRefreshing,
                onRefresh = { actualViewModel.onEvent(AdminEvent.RefreshData) },
                onNotifyAll = {
                    selectedUser = null
                    showNotificationDialog = true
                }
            )

            // Lista de usuarios
            if (uiState.users.isEmpty() && !uiState.isLoading) {
                ExecutiveEmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
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
                            }
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

@Composable
fun ExecutiveHeader(onLogout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = ExecutiveTheme.RichBlack,
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExecutiveTheme.PrimaryGradient)
                .padding(horizontal = 32.dp, vertical = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = ExecutiveTheme.PremiumGold.copy(alpha = 0.15f),
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ExecutiveTheme.GlassEffect),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = ExecutiveTheme.PremiumGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "EXPENSE TRACKER",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = ExecutiveTheme.PureWhite,
                                letterSpacing = 2.sp,
                                fontSize = 24.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Panel de Administración Ejecutivo",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = ExecutiveTheme.PremiumGold,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Salir de la App
                    Button(
                        onClick = {
                            // Aquí puedes agregar la lógica para salir de la aplicación
                            // Por ejemplo: (context as Activity).finishAffinity()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExecutiveTheme.WarningAmber.copy(alpha = 0.15f),
                            contentColor = ExecutiveTheme.WarningAmber
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SALIR APP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.8.sp
                        )
                    }

                    // Botón Logout
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExecutiveTheme.DangerRed.copy(alpha = 0.15f),
                            contentColor = ExecutiveTheme.DangerRed
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CERRAR SESIÓN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumDashboardCard(card: DashboardCardData) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 12.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ExecutiveTheme.CardGradient)
                .padding(24.dp)
        ) {
            // Línea de acento superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                card.color,
                                card.color.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.TopCenter)
            )

            // Icono elegante
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = card.color.copy(alpha = 0.12f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        card.icon,
                        contentDescription = null,
                        tint = card.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.75f)
            ) {
                Text(
                    text = card.value,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = ExecutiveTheme.PureWhite,
                        fontSize = 32.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = card.label.uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ExecutiveTheme.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun UsersSectionHeader(
    userCount: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNotifyAll: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "GESTIÓN DE USUARIOS",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = ExecutiveTheme.PureWhite,
                            letterSpacing = 2.sp,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$userCount usuarios registrados • Panel ejecutivo",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = ExecutiveTheme.LightGray,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = ExecutiveTheme.PremiumGold,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    ExecutiveTheme.ExecutiveGray.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Actualizar",
                                tint = ExecutiveTheme.PremiumGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    ExecutiveActionButton(
                        text = "NOTIFICAR A TODOS",
                        icon = Icons.Default.NotificationsActive,
                        onClick = onNotifyAll
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Línea separadora elegante
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ExecutiveTheme.PremiumGold.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun ExecutiveUserCard(
    user: User,
    onView: () -> Unit,
    onDelete: () -> Unit,
    onNotify: () -> Unit,
    onToggleStatus: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (user.isActive)
                    ExecutiveTheme.PremiumGold.copy(alpha = 0.3f)
                else
                    ExecutiveTheme.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExecutiveTheme.AccentGradient)
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header del usuario con status indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Nombre con indicador de estado
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(12.dp),
                                shape = CircleShape,
                                color = if (user.isActive) ExecutiveTheme.SuccessGreen else ExecutiveTheme.DangerRed
                            ) {}

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = user.name.uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = ExecutiveTheme.PureWhite,
                                    fontSize = 20.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información organizada en grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            UserInfoRow(
                                icon = Icons.Default.Badge,
                                label = "Documento ID",
                                value = user.document,
                                color = ExecutiveTheme.InfoBlue
                            )

                            UserInfoRow(
                                icon = Icons.Default.Person,
                                label = "ID Usuario",
                                value = user.id,
                                color = ExecutiveTheme.PremiumGold
                            )

                            UserInfoRow(
                                icon = Icons.Default.DateRange,
                                label = "Registrado",
                                value = user.registeredAt,
                                color = ExecutiveTheme.SuccessGreen
                            )
                        }
                    }

                    // Control de estado sofisticado
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = if (user.isActive)
                                ExecutiveTheme.SuccessGreen.copy(alpha = 0.15f)
                            else
                                ExecutiveTheme.DangerRed.copy(alpha = 0.15f),
                            shadowElevation = 4.dp
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (user.isActive) Icons.Default.CheckCircle else Icons.Default.Block,
                                    contentDescription = null,
                                    tint = if (user.isActive) ExecutiveTheme.SuccessGreen else ExecutiveTheme.DangerRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Switch(
                            checked = user.isActive,
                            onCheckedChange = onToggleStatus,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ExecutiveTheme.PureWhite,
                                checkedTrackColor = ExecutiveTheme.SuccessGreen,
                                uncheckedThumbColor = ExecutiveTheme.PureWhite,
                                uncheckedTrackColor = ExecutiveTheme.DangerRed.copy(alpha = 0.7f)
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (user.isActive) "ACTIVO" else "INACTIVO",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (user.isActive) ExecutiveTheme.SuccessGreen else ExecutiveTheme.DangerRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción ejecutivos mejorados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón Ver
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = ExecutiveTheme.InfoBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    ) {
                        Button(
                            onClick = onView,
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = ExecutiveTheme.InfoBlue
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Botón Notificar
                    Surface(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = if (user.isActive)
                            ExecutiveTheme.PremiumGold.copy(alpha = 0.15f)
                        else
                            ExecutiveTheme.LightGray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = if (user.isActive) 4.dp else 0.dp
                    ) {
                        Button(
                            onClick = onNotify,
                            modifier = Modifier.fillMaxSize(),
                            enabled = user.isActive,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = if (user.isActive) ExecutiveTheme.PremiumGold else ExecutiveTheme.LightGray.copy(alpha = 0.5f),
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = ExecutiveTheme.LightGray.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NOTIFICAR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Botón Eliminar
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = ExecutiveTheme.DangerRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    ) {
                        Button(
                            onClick = onDelete,
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = ExecutiveTheme.DangerRed
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ELIMINAR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.15f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ExecutiveTheme.LightGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ExecutiveTheme.PureWhite,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun ExecutiveActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = ExecutiveTheme.PremiumGold
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = if (enabled) 0.15f else 0.08f),
            contentColor = if (enabled) color else color.copy(alpha = 0.5f),
            disabledContainerColor = color.copy(alpha = 0.08f),
            disabledContentColor = color.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled) 4.dp else 0.dp,
            pressedElevation = 8.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun ExecutiveEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            color = ExecutiveTheme.ExecutiveGray.copy(alpha = 0.3f),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ExecutiveTheme.GlassEffect),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PersonOff,
                    contentDescription = null,
                    tint = ExecutiveTheme.LightGray,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "NO HAY USUARIOS REGISTRADOS",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = ExecutiveTheme.PureWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Los usuarios aparecerán aquí una vez se registren en la aplicación.\nEste panel ejecutivo te ayudará a gestionarlos de manera eficiente.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ExecutiveTheme.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun ExecutiveNotificationDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 20.dp,
            color = ExecutiveTheme.RichBlack
        ) {
            Box(
                modifier = Modifier
                    .background(ExecutiveTheme.AccentGradient)
                    .padding(32.dp)
            ) {
                Column {
                    // Header elegante
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = ExecutiveTheme.PremiumGold.copy(alpha = 0.15f),
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = ExecutiveTheme.PremiumGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Text(
                                text = if (user == null) "NOTIFICAR A TODOS" else "NOTIFICAR USUARIO",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = ExecutiveTheme.PureWhite,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            if (user != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = user.name.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = ExecutiveTheme.PremiumGold,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Campo de texto premium
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = {
                            Text(
                                "MENSAJE DE NOTIFICACIÓN",
                                color = ExecutiveTheme.LightGray,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        },
                        placeholder = {
                            Text(
                                "Ingresa tu mensaje de notificación ejecutiva...",
                                color = ExecutiveTheme.LightGray.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ExecutiveTheme.PremiumGold,
                            unfocusedBorderColor = ExecutiveTheme.LightGray.copy(alpha = 0.3f),
                            focusedTextColor = ExecutiveTheme.PureWhite,
                            unfocusedTextColor = ExecutiveTheme.PureWhite,
                            cursorColor = ExecutiveTheme.PremiumGold,
                            focusedLabelColor = ExecutiveTheme.PremiumGold,
                            unfocusedLabelColor = ExecutiveTheme.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botones de acción ejecutivos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ExecutiveTheme.LightGray
                            ),
                            border = BorderStroke(
                                1.5.dp, ExecutiveTheme.LightGray.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "CANCELAR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (message.isNotBlank()) {
                                    onSend(message)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ExecutiveTheme.PremiumGold,
                                contentColor = ExecutiveTheme.DeepNavy,
                                disabledContainerColor = ExecutiveTheme.LightGray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            enabled = message.isNotBlank(),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "ENVIAR",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutiveFooter(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExecutiveTheme.PrimaryGradient)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EXPENSE TRACKER PANEL EJECUTIVO",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        color = ExecutiveTheme.PremiumGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "© 2025 • Administración v2.0 • Confidencial",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = ExecutiveTheme.LightGray,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

// Enum para tipos de notificación
enum class NotificationType {
    Success, Error, Warning, Info
}

@Composable
fun ExecutiveNotificationCard(
    message: String,
    type: NotificationType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, iconColor, icon) = when (type) {
        NotificationType.Success -> Triple(
            ExecutiveTheme.SuccessGreen.copy(alpha = 0.9f),
            ExecutiveTheme.PureWhite,
            Icons.Default.CheckCircle
        )
        NotificationType.Error -> Triple(
            ExecutiveTheme.DangerRed.copy(alpha = 0.9f),
            ExecutiveTheme.PureWhite,
            Icons.Default.Error
        )
        NotificationType.Warning -> Triple(
            ExecutiveTheme.WarningAmber.copy(alpha = 0.9f),
            ExecutiveTheme.DeepNavy,
            Icons.Default.Warning
        )
        NotificationType.Info -> Triple(
            ExecutiveTheme.InfoBlue.copy(alpha = 0.9f),
            ExecutiveTheme.PureWhite,
            Icons.Default.Info
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = ExecutiveTheme.PureWhite.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = message,
                color = iconColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

// Data class para las cards del dashboard
data class DashboardCardData(
    val value: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)


private fun getDashboardCards(userCount: Int, dashboardStats: Any?): List<DashboardCardData> {
    val activeUsers = dashboardStats?.let {
        0
    } ?: 0

    return listOf(
        DashboardCardData(
            value = userCount.toString(),
            label = "Usuarios Totales",
            icon = Icons.Default.People,
            color = ExecutiveTheme.InfoBlue
        ),
        DashboardCardData(
            value = activeUsers.toString(),
            label = "Usuarios Activos",
            icon = Icons.Default.Person,
                    color = ExecutiveTheme.SuccessGreen
        ),
        DashboardCardData(
            value = "0",
            label = "Notificaciones Enviadas",
            icon = Icons.Default.NotificationsActive,
            color = ExecutiveTheme.PremiumGold
        )
    )
}