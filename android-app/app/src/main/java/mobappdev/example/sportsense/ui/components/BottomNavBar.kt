package mobappdev.example.sportsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
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
    NavigationBar(
        containerColor = Color(0xFF1E1E1E), // Mörkgrå färg på navbar
        tonalElevation = 8.dp, // Lägger en liten skugga för bättre design
        modifier = Modifier.fillMaxWidth()
    ) {
        val items = listOf(
            Screen.Home,
            Screen.History,
            Screen.Settings
        )

        items.forEachIndexed { index, screen ->
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color(0xFF2A2A2A)) // Färgen mellan ikonerna
            ) {
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.label) },
                    label = { Text(screen.label) },
                    selected = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = screen.color,
                        unselectedIconColor = screen.color.copy(alpha = 0.6f)
                    ),
                    onClick = { navController.navigate(screen.route) }
                )
            }

            if (index < items.size - 1) { // Lägger till streck mellan ikoner
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}