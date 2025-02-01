package mobappdev.example.sportsense.ui.screens

import android.content.Context
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
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.ui.theme.LightBlue80
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    var sensorHistory by remember { mutableStateOf(SensorStorage.getSensorHistory(context)) }
    var isFilterVisible by remember { mutableStateOf(false) }
    var dateFilter by remember { mutableStateOf("") }
    var hrFilter by remember { mutableStateOf("") }

    // Statiska färger som fungerar i både dark mode och light mode
    val backgroundColor = Color.Black
    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5)) // Blå gradient
    )
    val textColor = Color.White
    val headerTextColor = Color(0xFF0D47A1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header med titel och knappar
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
                    SensorStorage.clearHistory(context)
                    sensorHistory = emptyList()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = Color.Red)
                }

                IconButton(onClick = {
                    sensorHistory = SensorStorage.getSensorHistory(context)
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.Green)
                }
            }
        }

        // Filtersektion
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
            Text(text = "No sensor data recorded", color = Color.Gray)
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
                                            val updatedHistory = sensorHistory.toMutableList().apply {
                                                removeAt(index)
                                            }
                                            sensorHistory = updatedHistory
                                            SensorStorage.updateSensorHistory(context, updatedHistory)
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
                                        text = "Heart Rate: ${data.heartRate} BPM",
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
