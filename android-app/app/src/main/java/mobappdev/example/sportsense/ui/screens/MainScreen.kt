package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun MainScreen(vm: SensorVM, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6)) // Gradient från mörkblå till ljusblå
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Sensor Data",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            val sensorData = vm.sensorData.collectAsState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Heart Rate: ${sensorData.value.heartRate}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "Accelerometer: ${sensorData.value.accelX}, ${sensorData.value.accelY}, ${sensorData.value.accelZ}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = "Gyroscope: ${sensorData.value.gyroX}, ${sensorData.value.gyroY}, ${sensorData.value.gyroZ}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ButtonWithIcon(
                text = "History",
                icon = Icons.Filled.History,
                color = Color.Red,
                onClick = { navController.navigate("history") }
            )
            ButtonWithIcon(
                text = "Train AI Model",
                icon = Icons.Filled.Science,
                color = Color.Green,
                onClick = { navController.navigate("train_ai") }
            )
            ButtonWithIcon(
                text = "Import AI Model",
                icon = Icons.Filled.Download,
                color = Color.Cyan,
                onClick = { navController.navigate("import_model") }
            )
            ButtonWithIcon(
                text = "Settings",
                icon = Icons.Filled.Settings,
                color = Color.Yellow,
                onClick = { navController.navigate("settings") }
            )
        }
    }
}

@Composable
fun ButtonWithIcon(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.Black)
        }
    }
}
