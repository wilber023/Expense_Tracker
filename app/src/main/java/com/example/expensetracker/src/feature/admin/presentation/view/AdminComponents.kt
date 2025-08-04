package com.example.expensetracker.src.feature.admin.presentation.components

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.BackHandler
import com.example.expensetracker.src.feature.admin.domain.model.User
import com.example.expensetracker.src.feature.admin.presentation.theme.ExecutiveTheme
import com.example.expensetracker.src.feature.admin.presentation.theme.DashboardCardData
import com.example.expensetracker.src.feature.admin.presentation.theme.NotificationType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@Composable
fun ExecutiveHeader(onLogout: () -> Unit) {
    val context = LocalContext.current


    val exitApp: () -> Unit = {
        onLogout()

        (context as? ComponentActivity)?.let { activity ->
            activity.finishAffinity()
        }
    }


    BackHandler {
        exitApp()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExecutiveTheme.PrimaryGradient)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        color = ExecutiveTheme.PremiumGold.copy(alpha = 0.15f),
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = ExecutiveTheme.PremiumGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "EXPENSE TRACKER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = ExecutiveTheme.PureWhite,
                                letterSpacing = 1.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Panel de Administración",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ExecutiveTheme.PremiumGold,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = exitApp,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExecutiveTheme.DangerRed.copy(alpha = 0.15f),
                            contentColor = ExecutiveTheme.DangerRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SALIR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
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
            .width(180.dp)
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ExecutiveTheme.CardGradient)
                .padding(20.dp)
        ) {

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = card.color.copy(alpha = 0.15f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        card.icon,
                        contentDescription = null,
                        tint = card.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }


            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = card.value,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = ExecutiveTheme.PureWhite,
                        fontSize = 28.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ExecutiveTheme.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(card.color.copy(alpha = 0.6f))
                    .align(Alignment.TopCenter)
            )
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "GESTIÓN DE USUARIOS",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = ExecutiveTheme.PureWhite,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$userCount usuarios registrados",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ExecutiveTheme.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón refresh
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            ExecutiveTheme.ExecutiveGray.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    enabled = !isRefreshing
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = ExecutiveTheme.PremiumGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = ExecutiveTheme.PremiumGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Botón notificar todos
                Button(
                    onClick = onNotifyAll,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExecutiveTheme.PremiumGold.copy(alpha = 0.15f),
                        contentColor = ExecutiveTheme.PremiumGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NOTIFICAR TODOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Línea separadora
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ExecutiveTheme.PremiumGold.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun ExecutiveUserCard(
    user: User,
    onView: () -> Unit,
    onDelete: () -> Unit,
    onNotify: () -> Unit,
    onToggleStatus: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (user.isActive)
                    ExecutiveTheme.PremiumGold.copy(alpha = 0.3f)
                else
                    ExecutiveTheme.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ExecutiveTheme.AccentGradient)
                .padding(24.dp)
        ) {
            Column {
                // Header del usuario
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
                                modifier = Modifier.size(10.dp),
                                shape = CircleShape,
                                color = if (user.isActive) ExecutiveTheme.SuccessGreen else ExecutiveTheme.DangerRed
                            ) {}

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = user.username.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ExecutiveTheme.PureWhite
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información del usuario
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserInfoRow(
                                icon = Icons.Default.Badge,
                                label = "Rol",
                                value = user.role.uppercase(Locale.getDefault()),
                                color = ExecutiveTheme.InfoBlue
                            )

                            UserInfoRow(
                                icon = Icons.Default.Person,
                                label = "ID",
                                value = user.id.toString(),
                                color = ExecutiveTheme.PremiumGold
                            )

                            UserInfoRow(
                                icon = Icons.Default.DateRange,
                                label = "Registro",
                                value = formatDate(user.createdAt),
                                color = ExecutiveTheme.SuccessGreen
                            )
                        }
                    }

                    // Control de estado
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = if (user.isActive)
                                ExecutiveTheme.SuccessGreen.copy(alpha = 0.15f)
                            else
                                ExecutiveTheme.DangerRed.copy(alpha = 0.15f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (user.isActive) Icons.Default.CheckCircle else Icons.Default.Block,
                                    contentDescription = null,
                                    tint = if (user.isActive) ExecutiveTheme.SuccessGreen else ExecutiveTheme.DangerRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

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

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (user.isActive) "ACTIVO" else "INACTIVO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (user.isActive) ExecutiveTheme.SuccessGreen else ExecutiveTheme.DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones de acción reorganizados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Ver
                    ActionButton(
                        text = "VER",
                        icon = Icons.Default.Visibility,
                        onClick = onView,
                        color = ExecutiveTheme.InfoBlue,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón Notificar
                    ActionButton(
                        text = "NOTIFICAR",
                        icon = Icons.Default.Notifications,
                        onClick = onNotify,
                        color = ExecutiveTheme.PremiumGold,
                        enabled = user.isActive,
                        modifier = Modifier.weight(1.2f)
                    )

                    // Botón Eliminar
                    ActionButton(
                        text = "ELIMINAR",
                        icon = Icons.Default.Delete,
                        onClick = onDelete,
                        color = ExecutiveTheme.DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = if (enabled) 0.15f else 0.08f),
            contentColor = if (enabled) color else color.copy(alpha = 0.5f),
            disabledContainerColor = color.copy(alpha = 0.08f),
            disabledContentColor = color.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled) 2.dp else 0.dp
        )
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
            modifier = Modifier.size(20.dp),
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
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ExecutiveTheme.LightGray,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ExecutiveTheme.PureWhite,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = ExecutiveTheme.ExecutiveGray.copy(alpha = 0.3f),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PersonOff,
                    contentDescription = null,
                    tint = ExecutiveTheme.LightGray,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "NO HAY USUARIOS REGISTRADOS",
            style = MaterialTheme.typography.titleLarge.copy(
                color = ExecutiveTheme.PureWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Los usuarios aparecerán aquí una vez se registren en la aplicación.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ExecutiveTheme.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
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
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 16.dp,
            color = ExecutiveTheme.RichBlack
        ) {
            Box(
                modifier = Modifier
                    .background(ExecutiveTheme.AccentGradient)
                    .padding(28.dp)
            ) {
                Column {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = ExecutiveTheme.PremiumGold.copy(alpha = 0.15f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = ExecutiveTheme.PremiumGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = if (user == null) "NOTIFICAR A TODOS" else "NOTIFICAR USUARIO",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ExecutiveTheme.PureWhite
                                )
                            )
                            if (user != null) {
                                Text(
                                    text = user.username,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = ExecutiveTheme.PremiumGold,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de texto
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = {
                            Text(
                                "Mensaje de notificación",
                                color = ExecutiveTheme.LightGray
                            )
                        },
                        placeholder = {
                            Text(
                                "Ingresa tu mensaje...",
                                color = ExecutiveTheme.LightGray.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ExecutiveTheme.PremiumGold,
                            unfocusedBorderColor = ExecutiveTheme.LightGray.copy(alpha = 0.3f),
                            focusedTextColor = ExecutiveTheme.PureWhite,
                            unfocusedTextColor = ExecutiveTheme.PureWhite,
                            cursorColor = ExecutiveTheme.PremiumGold
                        ),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ExecutiveTheme.LightGray
                            ),
                            border = BorderStroke(
                                1.dp, ExecutiveTheme.LightGray.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "CANCELAR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
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
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ExecutiveTheme.PremiumGold,
                                contentColor = ExecutiveTheme.DeepNavy
                            ),
                            shape = RoundedCornerShape(16.dp),
                            enabled = message.isNotBlank(),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ENVIAR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
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
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 12.dp,
        color = ExecutiveTheme.RichBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExecutiveTheme.PrimaryGradient)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EXPENSE TRACKER ADMIN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ExecutiveTheme.PremiumGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "© 2025 • Versión 2.0",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ExecutiveTheme.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
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
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
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
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = iconColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}


private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString.substring(0, 10)
    }
}