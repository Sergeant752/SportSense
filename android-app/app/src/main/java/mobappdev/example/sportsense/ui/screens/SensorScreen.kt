package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun SensorScreen(
    vm: SensorVM
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Sensor Data",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        val sensorData = vm.sensorData.collectAsState()
        Text(
            text = "Heart Rate: ${sensorData.value.heartRate}\n" +
                    "Accelerometer: ${sensorData.value.accelX}, ${sensorData.value.accelY}, ${sensorData.value.accelZ}\n" +
                    "Gyroscope: ${sensorData.value.gyroX}, ${sensorData.value.gyroY}, ${sensorData.value.gyroZ}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = vm::fetchNewSensorData
        ) {
            Text(text = "Fetch Data")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
