package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun ScanScreen(vm: SensorVM, navController: NavController) {
    var isScanning by remember { mutableStateOf(true) }
    val scannedDevices by vm.devices.collectAsState()
    val connectedDevice by vm.connectedDevice.collectAsState()

    LaunchedEffect(Unit) {
        vm.startScanning()
        delay(3000) // Simulera scanningstid
        isScanning = false
    }

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
                text = if (isScanning) "Scanning for Devices..."
                else if (scannedDevices.isNotEmpty()) "Device found!"
                else "No devices found",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isScanning) {
                CircularProgressIndicator(color = Color.White)
            } else {
                if (scannedDevices.isEmpty()) {
                    Text("No devices found", color = Color.White)
                } else {
                    LazyColumn {
                        items(scannedDevices) { device ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        val deviceId = device.substringAfter("(").substringBefore(")")
                                        vm.connectToDevice(deviceId)
                                        navController.navigate("home")
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.9f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Device: $device",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
