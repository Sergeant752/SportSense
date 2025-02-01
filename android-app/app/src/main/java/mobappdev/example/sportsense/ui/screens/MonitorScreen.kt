package mobappdev.example.sportsense.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun MonitorScreen(vm: SensorVM, navController: NavController, deviceId: String) {
    val heartRate by vm.heartRate.collectAsState()
    val sensorData by vm.sensorData.collectAsState()

    LaunchedEffect(sensorData) {
        Log.d("MonitorScreen", "Sensor Data Updated: $sensorData")
    }

    var isMeasuringHR by remember { mutableStateOf(false) }
    var isMeasuringACC by remember { mutableStateOf(false) }
    var isMeasuringGYRO by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
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
                text = "Sensor Monitoring - $deviceId",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Heart Rate: ${heartRate} bpm",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ACC (X:${sensorData.accelX}, Y:${sensorData.accelY}, Z:${sensorData.accelZ})",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "GYRO (X:${sensorData.gyroX}, Y:${sensorData.gyroY}, Z:${sensorData.gyroZ})",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isMeasuringHR) {
                        vm.stopHeartRateMeasurement()
                    } else {
                        vm.startHeartRateMeasurement()
                    }
                    isMeasuringHR = !isMeasuringHR
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeasuringHR) Color.Red else Color.Green
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isMeasuringHR) "Stop HR Measurement" else "Start HR Measurement")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (isMeasuringACC) {
                        vm.stopAccelerometerMeasurement()
                    } else {
                        vm.startAccelerometerMeasurement()
                    }
                    isMeasuringACC = !isMeasuringACC
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeasuringACC) Color.Red else Color.Green
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isMeasuringACC) "Stop ACC Measurement" else "Start ACC Measurement")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (isMeasuringGYRO) {
                        vm.stopGyroscopeMeasurement()
                    } else {
                        vm.startGyroscopeMeasurement()
                    }
                    isMeasuringGYRO = !isMeasuringGYRO
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeasuringGYRO) Color.Red else Color.Green
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isMeasuringGYRO) "Stop GYRO Measurement" else "Start GYRO Measurement")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.stopAllMeasurements()
                    isMeasuringHR = false
                    isMeasuringACC = false
                    isMeasuringGYRO = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop All Measurements")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Scan")
            }
        }
    }
}