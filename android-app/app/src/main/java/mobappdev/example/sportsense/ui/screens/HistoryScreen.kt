package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.ui.viewmodels.SensorVM
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController, vm: SensorVM, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val db = SensorDatabase.getDatabase(context)
    val dao = db.sensorDao()
    val coroutineScope = rememberCoroutineScope()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    var sensorHistory by remember { mutableStateOf(listOf<SensorData>()) }
    var isFilterVisible by remember { mutableStateOf(false) }
    var dateFilter by remember { mutableStateOf("") }
    var hrFilter by remember { mutableStateOf("") }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            Toast.makeText(context, "Sign in/Register to access this page", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
    }
    if (!isLoggedIn) return

    LaunchedEffect(Unit) {
        sensorHistory = dao.getAllSensorData()
        coroutineScope.launch {
            vm.fetchAnalyzedData(context)
        }
    }
    val backgroundColor = Color.Black
    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
    )
    val textColor = Color.White
    Column(
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
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Sensor History",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Yellow
            )
            Row {
                IconButton(onClick = { isFilterVisible = !isFilterVisible }) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Blue)
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        dao.clearSensorData()
                        sensorHistory = emptyList()
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = Color.Red)
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        sensorHistory = dao.getAllSensorData()
                    }
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.Green)
                }
                IconButton(onClick = { navController.navigate("hr_graph") }) {
                    Icon(Icons.Default.ShowChart, contentDescription = "HR Graph", tint = Color.Magenta)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val csvPath = vm.exportDataAsCSV(context)
                            Toast.makeText(context, "Data exported to CSV at $csvPath", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export CSV", tint = Color.Green)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val jsonPath = vm.exportDataAsJSON(context)
                            Toast.makeText(context, "Data exported to JSON at $jsonPath", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Code, contentDescription = "Export JSON", tint = Color.Cyan)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val recordsProcessed = vm.sendDataToPython(context)
                            Toast.makeText(context, "Python analysis complete: $recordsProcessed records", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Send to Python", tint = Color.Magenta)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            vm.fetchAnalyzedData(context)
                            Toast.makeText(context, "Fetched analyzed data from Python", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.CloudDownload, contentDescription = "Fetch Analysis", tint = Color.Blue)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            vm.downloadModel(context)
                            Toast.makeText(context, "ML Model downloaded successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download Model", tint = Color.Yellow)
                    }
                }
            }
        }
        AnimatedVisibility(visible = isFilterVisible) {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                OutlinedTextField(
                    value = dateFilter,
                    onValueChange = { dateFilter = it },
                    label = { Text("Filter by Date (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hrFilter,
                    onValueChange = { hrFilter = it },
                    label = { Text("Filter by Heart Rate") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        val filteredHistory = sensorHistory.filter { data ->
            (dateFilter.isEmpty() || getFormattedDate(data.timestamp).startsWith(dateFilter)) &&
                    (hrFilter.isEmpty() || data.heartRate.toString() == hrFilter)
        }
        if (filteredHistory.isEmpty()) {
            Text(text = "No sensor data recorded", color = Color.Yellow)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(filteredHistory) { index, data ->
                    var isVisible by remember { mutableStateOf(true) }
                    AnimatedVisibility(visible = isVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardGradient)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures { _, dragAmount ->
                                        if (dragAmount > 100 || dragAmount < -100) {
                                            isVisible = false
                                            coroutineScope.launch {
                                                dao.deleteSensorData(data)
                                                sensorHistory = dao.getAllSensorData()
                                            }
                                        }
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Heart Rate", tint = Color.Red)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Heart rate: ${data.heartRate} BPM",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = textColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsRun, contentDescription = "Accelerometer", tint = Color.Green)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ACC: X = ${data.accelX}, Y = ${data.accelY}, Z = ${data.accelZ}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.RotateRight, contentDescription = "Gyroscope", tint = Color.Yellow)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "GYRO: X = ${data.gyroX}, Y = ${data.gyroY}, Z = ${data.gyroZ}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsRun, contentDescription = "Movement", tint = Color.Cyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Movement: ${data.movementDetected ?: "Unknown"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Timestamp", tint = Color.Magenta)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Time: ${getFormattedDate(data.timestamp)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor.copy(alpha = 0.9f)
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

fun getFormattedDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}