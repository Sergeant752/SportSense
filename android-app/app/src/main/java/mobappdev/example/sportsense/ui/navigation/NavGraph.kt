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
    val chatVM: ChatVM = viewModel()  // Skapa en instans av ChatVM

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel,
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                userViewModel = userViewModel,
                onRegisterSuccess = { navController.navigate("login") }
            )
        }

        composable("home") {
            MainScreen(vm = sensorVM, userViewModel = userViewModel ,navController = navController)
        }

        composable("scan") { ScanScreen(vm = sensorVM, navController = navController) }

        composable("history") { HistoryScreen(navController = navController, vm = sensorVM, userViewModel = userViewModel) }

        composable("train_ai") { TrainAIScreen() }

        composable("import_model") { ImportModelScreen() }

        composable("settings") { SettingsScreen(navController = navController, vm = sensorVM, userViewModel = userViewModel) }

        composable("hr_graph") {
            val sensorData = remember { mutableStateOf<List<SensorData>>(emptyList()) }
            LaunchedEffect(Unit) {
                sensorData.value = sensorVM.getAllSensorData()
            }
            HRGraphScreen(sensorData = sensorData.value)
        }

        composable(
            route = "monitor/{deviceId}",
            arguments = listOf(navArgument("deviceId") { defaultValue = "Unknown Device" })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: "Unknown Device"
            MonitorScreen(vm = sensorVM, navController = navController, deviceId = deviceId)
        }

        composable("others") { OtherScreen(navController = navController, userViewModel = userViewModel) }

        composable("user_list") {
            val username = userViewModel.getCurrentUser() ?: "Guest"
            UserListScreen(navController = navController, userViewModel = userViewModel, currentUser = username)
        }

        // 🔹 Uppdaterad chat-navigation
        composable(
            route = "chat/{currentUser}/{recipient}",
            arguments = listOf(
                navArgument("currentUser") { defaultValue = "Guest" },
                navArgument("recipient") { defaultValue = "Unknown" }
            ),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { backStackEntry ->
            val chatVM: ChatVM = viewModel()
            val currentUser = backStackEntry.arguments?.getString("currentUser") ?: "Guest"
            val recipient = backStackEntry.arguments?.getString("recipient") ?: "Unknown"

            ChatScreen(
                chatVM = chatVM,
                userViewModel = userViewModel,
                navController = navController,
                currentUser = currentUser,
                recipient = recipient
            )
        }
    }
}