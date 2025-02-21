package mobappdev.example.sportsense.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.ui.screens.*
import mobappdev.example.sportsense.ui.viewmodels.ChatVM
import mobappdev.example.sportsense.ui.viewmodels.SensorVM
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel

@Composable
fun NavGraph(navController: NavHostController, sensorVM: SensorVM, userViewModel: UserViewModel) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(
            "login",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel,
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable(
            "register",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            RegisterScreen(
                navController = navController,
                userViewModel = userViewModel,
                onRegisterSuccess = { navController.navigate("login") }
            )
        }

        composable(
            "home",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { MainScreen(vm = sensorVM, userViewModel = userViewModel ,navController = navController) }

        composable(
            "scan",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { ScanScreen(vm = sensorVM, navController = navController) }

        composable(
            "history",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { HistoryScreen(navController = navController, vm = sensorVM , userViewModel = userViewModel) }

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
        ) { SettingsScreen(navController = navController,vm = sensorVM, userViewModel = userViewModel) }

        composable(
            "hr_graph",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            val sensorData = remember { mutableStateOf<List<SensorData>>(emptyList()) }
            LaunchedEffect(Unit) {
                sensorData.value = sensorVM.getAllSensorData()
            }
            HRGraphScreen(sensorData = sensorData.value)
        }

        composable(
            route = "monitor/{deviceId}",
            arguments = listOf(navArgument("deviceId") { defaultValue = "Unknown Device" }),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: "Unknown Device"
            MonitorScreen(vm = sensorVM, navController = navController, deviceId = deviceId)
        }

        composable(
            "others",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            OtherScreen(navController = navController, userViewModel = userViewModel)
        }

        composable("chat") {
            val chatVM: ChatVM = viewModel()
            val username = userViewModel.getCurrentUser() ?: "Guest"
            ChatScreen(vm = chatVM, navController = navController, username = username)
        }
    }
}