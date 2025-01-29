package mobappdev.example.sportsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import mobappdev.example.sportsense.ui.components.BottomNavBar
import mobappdev.example.sportsense.ui.components.TopBar
import mobappdev.example.sportsense.ui.navigation.NavGraph
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sensorViewModel = SensorVM(application = application)
            val navController = rememberNavController()

            Scaffold(
                topBar = { TopBar(title = "SportSense") },
                bottomBar = { BottomNavBar(navController) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavGraph(navController = navController, sensorVM = sensorViewModel)
                }
            }
        }
    }
}