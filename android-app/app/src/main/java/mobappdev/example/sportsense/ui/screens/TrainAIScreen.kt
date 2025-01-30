package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrainAIScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Train AI Model", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Export sensor data to Python */ }) {
            Text("Export Data for Training")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* Trigger ML training process */ }) {
            Text("Start Training")
        }
    }
}