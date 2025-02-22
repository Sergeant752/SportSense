package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.ui.viewmodels.SensorVM
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.utils.TFLiteModel

@Composable
fun MonitorScreen(vm: SensorVM, navController: NavController, deviceId: String) {
    val heartRate by vm.heartRate.collectAsState()
    val sensorData by vm.sensorData.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isMeasuringHR by remember { mutableStateOf(false) }
    var isMeasuringACC by remember { mutableStateOf(false) }
    var isMeasuringGYRO by remember { mutableStateOf(false) }
    val db = SensorDatabase.getDatabase(context)
    val dao = db.sensorDao()
    var predictedMovement by remember { mutableStateOf("No prediction yet") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B3D91), Color(0xFF1E3A8A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Sensor data - $deviceId",
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Prediction: $predictedMovement", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(
                    icon = Icons.Default.FileDownload,
                    label = "Export CSV",
                    color = Color.Green
                ) {
                    coroutineScope.launch {
                        val csvPath = vm.exportDataAsCSV(context)
                        Toast.makeText(context, "Data exported to CSV at $csvPath", Toast.LENGTH_SHORT).show()
                    }
                }
                IconWithLabel(
                    icon = Icons.Default.Code,
                    label = "Export JSON",
                    color = Color.Cyan
                ) {
                    coroutineScope.launch {
                        val jsonPath = vm.exportDataAsJSON(context)
                        Toast.makeText(context, "Data exported to JSON at $jsonPath", Toast.LENGTH_SHORT).show()
                    }
                }
                IconWithLabel(
                    icon = Icons.Default.CloudUpload,
                    label = "Send to Python",
                    color = Color.Magenta
                ) {
                    coroutineScope.launch {
                        val recordsProcessed = vm.sendDataToPython(context)
                        Toast.makeText(context, "Python analysis complete: $recordsProcessed records", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(
                    icon = Icons.Default.CloudDownload,
                    label = "Get Analysis",
                    color = Color.Blue
                ) {
                    coroutineScope.launch {
                        vm.fetchAnalyzedData(context)
                    }
                }
                IconWithLabel(
                    icon = Icons.Default.Download,
                    label = "Download Model",
                    color = Color.Yellow
                ) {
                    coroutineScope.launch {
                        vm.downloadModel(context)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(
                    icon = if (isMeasuringHR) Icons.Default.Stop else Icons.Default.Favorite,
                    label = if (isMeasuringHR) "Stop HR" else "Start HR",
                    color = if (isMeasuringHR) Color.Red else Color.Green
                ) {
                    if (isMeasuringHR) vm.stopHeartRateMeasurement(deviceId) else vm.startHeartRateMeasurement(deviceId)
                    isMeasuringHR = !isMeasuringHR
                }
                IconWithLabel(
                    icon = if (isMeasuringACC) Icons.Default.Stop else Icons.Default.DirectionsRun,
                    label = if (isMeasuringACC) "Stop ACC" else "Start ACC",
                    color = if (isMeasuringACC) Color.Red else Color.Green
                ) {
                    if (isMeasuringACC) vm.stopAccelerometerMeasurement(deviceId) else vm.startAccelerometerMeasurement(deviceId)
                    isMeasuringACC = !isMeasuringACC
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(
                    icon = if (isMeasuringGYRO) Icons.Default.Stop else Icons.Default.RotateRight,
                    label = if (isMeasuringGYRO) "Stop GYRO" else "Start GYRO",
                    color = if (isMeasuringGYRO) Color.Red else Color.Green
                ) {
                    if (isMeasuringGYRO) vm.stopGyroscopeMeasurement(deviceId) else vm.startGyroscopeMeasurement(deviceId)
                    isMeasuringGYRO = !isMeasuringGYRO
                }
                IconWithLabel(
                    icon = Icons.Default.Block,
                    label = "Stop All",
                    color = Color.Gray
                ) {
                    vm.stopAllMeasurements()
                    isMeasuringHR = false
                    isMeasuringACC = false
                    isMeasuringGYRO = false
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(
                    icon = Icons.Default.Save,
                    label = "Save Data",
                    color = Color.Blue
                ) {
                    coroutineScope.launch {
                        dao.insertSensorData(sensorData)
                        Toast.makeText(context, "Data saved to Room Database", Toast.LENGTH_SHORT).show()
                    }
                }
                IconWithLabel(
                    icon = Icons.Default.PlayArrow,
                    label = "Predict Movement",
                    color = Color.Yellow
                ) {
                    val inputData = listOf(
                        sensorData.accelX.toFloat(), sensorData.accelY.toFloat(), sensorData.accelZ.toFloat(),
                        sensorData.gyroX.toFloat(), sensorData.gyroY.toFloat(), sensorData.gyroZ.toFloat(),
                        sensorData.heartRate.toFloat()
                    ).flatMap { value -> List(600) { value } } // Skapa en fönsterstorlek på 600
                    if (inputData.size == 600 * 7) {
                        predictedMovement = vm.predictMovement(inputData)
                        Toast.makeText(context, predictedMovement, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Not enough data for prediction", Toast.LENGTH_SHORT).show()
                    }
                }
                IconWithLabel(
                    icon = Icons.Default.PowerSettingsNew,
                    label = "Disconnect",
                    color = Color.Red
                ) {
                    vm.disconnectDevice(deviceId)
                    Toast.makeText(context, "Device disconnected", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            IconWithLabel(
                icon = Icons.Default.ArrowBack,
                label = "Back Home",
                color = Color.LightGray
            ) {
                vm.stopAllMeasurements()
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun IconWithLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label, tint = color)
        }
        Text(text = label, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}