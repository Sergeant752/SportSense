package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun ScanScreen(vm: SensorVM, navController: NavController) {
    var isScanning by remember { mutableStateOf(true) }
    val scannedDevices by vm.devices.collectAsState()
    val connectedDevices by vm.connectedDevices.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.startScanning()
        delay(3000)
        isScanning = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF311B92),
                        Color(0xFF1B1F3B)
                    )
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
                text = if (isScanning) "Scanning for devices..."
                else if (scannedDevices.isNotEmpty()) "Devices found!"
                else "No devices found",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isScanning) {
                CircularProgressIndicator(color = Color.White)
            } else {
                if (scannedDevices.isEmpty()) {
                    Text("No devices found", color = Color.Yellow)
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
                                        navController.navigate("monitor/$deviceId")
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
            Spacer(modifier = Modifier.height(20.dp))
            if (connectedDevices.isNotEmpty()) {
                Text(
                    text = "Connected devices: ${connectedDevices.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Yellow
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        val path = vm.exportDataAsCSV(context)
                        Toast.makeText(context, "Data exported to CSV at $path", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black  // Textfärgen blir svart
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Data as CSV")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val path = vm.exportDataAsJSON(context)
                        Toast.makeText(context, "Data exported to JSON at $path", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Cyan,
                    contentColor = Color.Black  // Textfärgen blir svart
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Data as JSON")
            }
        }
    }
}