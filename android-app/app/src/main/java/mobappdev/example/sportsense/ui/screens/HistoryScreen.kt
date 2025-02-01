package mobappdev.example.sportsense.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.ui.theme.LightBlue40
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    var sensorHistory by remember { mutableStateOf(SensorStorage.getSensorHistory(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue40)
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
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Button(
                onClick = {
                    SensorStorage.clearHistory(context)
                    sensorHistory = emptyList()  // Uppdatera listan direkt
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Clear", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sensorHistory.isEmpty()) {
            Text(text = "No sensor data recorded", color = Color.White)
        } else {
            LazyColumn {
                items(sensorHistory) { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Heart Rate: ${data.heartRate} BPM", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                            Text(text = "ACC: (X:${data.accelX}, Y:${data.accelY}, Z:${data.accelZ})", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            Text(text = "GYRO: (X:${data.gyroX}, Y:${data.gyroY}, Z:${data.gyroZ})", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            Text(text = "Time: ${getFormattedDate(data.timestamp)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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