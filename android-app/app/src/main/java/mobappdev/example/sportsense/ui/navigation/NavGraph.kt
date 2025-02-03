package mobappdev.example.sportsense.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import mobappdev.example.sportsense.ui.screens.*
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun NavGraph(navController: NavHostController, sensorVM: SensorVM) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(
            "home",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { MainScreen(vm = sensorVM, navController = navController) }

        composable(
            "scan",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { ScanScreen(vm = sensorVM, navController = navController) }

        composable(
            "history",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { HistoryScreen() }

        composable(
            "train_ai",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { TrainAIScreen() }

        composable(
            "import_model",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { ImportModelScreen() }

        composable(
            "settings",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { SettingsScreen(vm = sensorVM) }

        composable(
            route = "monitor/{deviceId}",
            arguments = listOf(navArgument("deviceId") { defaultValue = "Unknown Device" }),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: "Unknown Device"
            MonitorScreen(vm = sensorVM, navController = navController, deviceId = deviceId)
        }
    }
}