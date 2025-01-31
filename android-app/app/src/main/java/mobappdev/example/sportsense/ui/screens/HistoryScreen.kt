package mobappdev.example.sportsense.ui.screens

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
import androidx.compose.ui.unit.dp
import mobappdev.example.sportsense.ui.theme.LightBlue40
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {
    val savedHRData = remember { mutableStateListOf<Pair<Int, String>>() }

    // Simulerad sparad HR-data (här kan du koppla din faktiska datakälla)
    LaunchedEffect(Unit) {
        savedHRData.add(Pair(75, getCurrentTimestamp()))
        savedHRData.add(Pair(82, getCurrentTimestamp()))
        savedHRData.add(Pair(90, getCurrentTimestamp()))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue40) // Bakgrundsfärg
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Heart Rate History",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (savedHRData.isEmpty()) {
            Text(text = "No HR data recorded", color = Color.White)
        } else {
            LazyColumn {
                items(savedHRData) { (hr, timestamp) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Heart Rate: $hr BPM", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                            Text(text = "Time: $timestamp", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
