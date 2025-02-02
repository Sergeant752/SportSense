package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun MonitorScreen(vm: SensorVM, navController: NavController, deviceId: String) {
    val heartRate by vm.heartRate.collectAsState()
    val sensorData by vm.sensorData.collectAsState()
    val context = LocalContext.current

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
                text = "Sensor monitoring - $deviceId",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Heart rate: ${heartRate} bpm", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ACC (X:${sensorData.accelX}, Y:${sensorData.accelY}, Z:${sensorData.accelZ})",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "GYRO (X:${sensorData.gyroX}, Y:${sensorData.gyroY}, Z:${sensorData.gyroZ})",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isMeasuringHR) {
                        vm.stopHeartRateMeasurement(deviceId)
                    } else {
                        vm.startHeartRateMeasurement(deviceId)
                    }
                    isMeasuringHR = !isMeasuringHR
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeasuringHR) Color.Red else Color.Green
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isMeasuringHR) "Stop HR measurement" else "Start HR measurement")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (isMeasuringACC) {
                        vm.stopAccelerometerMeasurement(deviceId)
                    } else {
                        vm.startAccelerometerMeasurement(deviceId)
                    }
                    isMeasuringACC = !isMeasuringACC
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeasuringACC) Color.Red else Color.Green
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isMeasuringACC) "Stop ACC measurement" else "Start ACC measurement")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (isMeasuringGYRO) {
                        vm.stopGyroscopeMeasurement(deviceId)
                    } else {
                        vm.startGyroscopeMeasurement(deviceId)
                    }
                    isMeasuringGYRO = !isMeasuringGYRO
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeasuringGYRO) Color.Red else Color.Green
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isMeasuringGYRO) "Stop GYRO measurement" else "Start GYRO measurement")
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
                Text("Stop all measurements")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val filePath = SensorStorage.exportSensorDataAsCSV(context)
                    Toast.makeText(context, "Data exported to $filePath", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export data as CSV", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val filePath = SensorStorage.exportSensorDataAsJSON(context)
                    Toast.makeText(context, "Data exported to $filePath", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export data as JSON", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.stopAllMeasurements()
                    vm.disconnectDevice(deviceId)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}