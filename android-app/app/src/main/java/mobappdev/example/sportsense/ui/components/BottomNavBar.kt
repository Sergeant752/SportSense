package mobappdev.example.sportsense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import mobappdev.example.sportsense.ui.theme.*

sealed class Screen(val route: String, val icon: ImageVector, val label: String, val color: Color) {
    object Home : Screen("home", Icons.Filled.Home, "Home", LiveDataColor)
    object History : Screen("history", Icons.Filled.History, "History", HistoryColor)
    object Settings : Screen("settings", Icons.Filled.Settings, "Settings", SettingsColor)
}

@Composable
fun BottomNavBar(navController: NavController) {
    var selectedRoute by remember { mutableStateOf("home") }

    NavigationBar(
        containerColor = Color(0xFF1E1E1E), // Mörkgrå navbar
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        val items = listOf(Screen.Home, Screen.History, Screen.Settings)

        items.forEach { screen ->
            val isSelected = screen.route == selectedRoute
            val animatedSize = animateFloatAsState(if (isSelected) 1.2f else 1f)
            val animatedColor = animateColorAsState(if (isSelected) screen.color else screen.color.copy(alpha = 0.6f))

            NavigationBarItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = screen.label,
                        tint = animatedColor.value,
                        modifier = Modifier.scale(animatedSize.value) // Skala ikonen vid val
                    )
                },
                label = { Text(screen.label) },
                selected = isSelected,
                onClick = {
                    selectedRoute = screen.route
                    navController.navigate(screen.route)
                }
            )
        }
    }
}
