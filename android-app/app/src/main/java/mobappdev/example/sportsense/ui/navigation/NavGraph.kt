package mobappdev.example.sportsense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mobappdev.example.sportsense.ui.screens.HistoryScreen
import mobappdev.example.sportsense.ui.screens.MainScreen
import mobappdev.example.sportsense.ui.screens.SettingsScreen
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun NavGraph(navController: NavHostController, sensorVM: SensorVM) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { MainScreen(vm = sensorVM) }
        composable("history") { HistoryScreen() }
        composable("settings") { SettingsScreen() }
    }
}