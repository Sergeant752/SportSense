package mobappdev.example.sportsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mobappdev.example.sportsense.ui.screens.SensorScreen
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val sensorViewModel = SensorVM(application = application)
                    SensorScreen(vm = sensorViewModel)
                }
            }
        }
    }
}