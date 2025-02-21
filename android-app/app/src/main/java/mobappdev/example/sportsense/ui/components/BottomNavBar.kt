package mobappdev.example.sportsense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import mobappdev.example.sportsense.ui.theme.LightBlue40

val BottomNavGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1B1F3B),
        Color(0xFF0D47A1)
    )
)

val HomeColor = LightBlue40
val HistoryColor = Color.Magenta
val SettingsColor = Color(0xFF4CAF50)

sealed class Screen(val route: String, val icon: ImageVector, val label: String, val color: Color) {
    object Home : Screen("home", Icons.Filled.Home, "Home", HomeColor)
    object History : Screen("history", Icons.Filled.History, "History", HistoryColor)
    object Settings : Screen("settings", Icons.Filled.Settings, "Settings", SettingsColor)
}

@Composable
fun BottomNavBar(navController: NavController) {
    var selectedRoute by remember { mutableStateOf("home") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomNavGradient)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            val items = listOf(Screen.Home, Screen.History, Screen.Settings)

            items.forEach { screen ->
                val isSelected = screen.route == selectedRoute
                val animatedSize by animateFloatAsState(if (isSelected) 1.2f else 1f)
                val animatedColor by animateColorAsState(if (isSelected) screen.color else screen.color.copy(alpha = 0.6f))

                NavigationBarItem(
                    icon = {
                        Icon(
                            screen.icon,
                            contentDescription = screen.label,
                            tint = animatedColor,
                            modifier = Modifier.scale(animatedSize)
                        )
                    },
                    label = {
                        Text(
                            screen.label,
                            color = Color.White
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        selectedRoute = screen.route
                        navController.navigate(screen.route)
                    }
                )
            }
        }
    }
}