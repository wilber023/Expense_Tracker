package com.example.expensetracker.src.feature.admin.presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


object ExecutiveTheme {

    val DeepNavy = Color(0xFF0A0E1A)
    val RichBlack = Color(0xFF0D1117)
    val EliteBlue = Color(0xFF1E3A8A)
    val PremiumGold = Color(0xFFD4AF37)
    val PlatinumSilver = Color(0xFFE5E7EB)
    val ExecutiveGray = Color(0xFF374151)
    val LightGray = Color(0xFF9CA3AF)
    val PureWhite = Color(0xFFFFFFFF)


    val SuccessGreen = Color(0xFF10B981)
    val DangerRed = Color(0xFFEF4444)
    val WarningAmber = Color(0xFFF59E0B)
    val InfoBlue = Color(0xFF3B82F6)


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

enum class NotificationType {
    Success, Error, Warning, Info
}


data class DashboardCardData(
    val value: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)


fun getDashboardCards(userCount: Int, dashboardStats: Any?): List<DashboardCardData> {
    val activeUsers = dashboardStats?.let { 0 } ?: (userCount * 0.8).toInt()

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
            label = "Notificaciones",
            icon = Icons.Default.NotificationsActive,
            color = ExecutiveTheme.PremiumGold
        )
    )
}