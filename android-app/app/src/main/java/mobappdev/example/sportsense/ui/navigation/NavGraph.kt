package mobappdev.example.sportsense.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
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
        ) { SettingsScreen() }

        composable(
            "hr_monitor/{deviceId}",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
            HRMonitorScreen(vm = sensorVM, navController = navController, deviceId = deviceId)
        }
    }
}