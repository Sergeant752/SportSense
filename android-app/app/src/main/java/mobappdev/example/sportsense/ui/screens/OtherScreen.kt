package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel

@Composable
fun OtherScreen(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            Toast.makeText(context, "Sign in/Register to access this page", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
    }

    if (!isLoggedIn) return

    var otherUsersData by remember { mutableStateOf(listOf<SensorData>()) }
    val db = SensorDatabase.getDatabase(context)
    val dao = db.sensorDao()

    LaunchedEffect(Unit) {
        otherUsersData = dao.getAllSensorData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF311B92), Color(0xFF1B1F3B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Other users' data",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Yellow,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(otherUsersData) { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1F3B))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Timestamp: ${data.timestamp}", color = Color.White)
                            Text(text = "Heart Rate: ${data.heartRate}", color = Color.White)
                            Text(text = "Movement: ${data.movementDetected}", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("chat") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
            ) {
                Text("Open Chat", color = Color.Black)
            }
        }
    }
}