package mobappdev.example.sportsense.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object LiveData : Screen("live_data", Icons.Filled.Home, "Live Data")
    object History : Screen("history", Icons.Filled.History, "History")
    object Settings : Screen("settings", Icons.Filled.Settings, "Settings")
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        val items = listOf(
            Screen.LiveData,
            Screen.History,
            Screen.Settings
        )

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = false, // Hantera selektion senare
                onClick = {
                    navController.navigate(screen.route)
                }
            )
        }
    }
}
