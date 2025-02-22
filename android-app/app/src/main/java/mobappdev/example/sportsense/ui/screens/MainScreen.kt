package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import mobappdev.example.sportsense.ui.viewmodels.SensorVM
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel

@Composable
fun MainScreen(vm: SensorVM, userViewModel: UserViewModel, navController: NavController) {
    val scannedDevices by vm.devices.collectAsState()
    val heartRate by vm.heartRate.collectAsState()
    val connectedDevices by vm.connectedDevices.collectAsState()
    val connectedDevice by vm.currentConnectedDevice.collectAsState()

    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            Toast.makeText(context, "Sign in/Register to access this page", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
    }

    if (!isLoggedIn) return

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
                text = "Explore",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (connectedDevice != null) {
                Text(
                    text = "Connected to: $connectedDevice",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate("monitor/${connectedDevice}") }) {
                    Text("Monitor: $connectedDevice")
                }
            } else {
                Button(
                    onClick = { navController.navigate("scan") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan for devices", color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth", tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ButtonWithIcon(
                text = "History",
                icon = Icons.Filled.History,
                color = Color.Red,
                onClick = { navController.navigate("history") }
            )
            ButtonWithIcon(
                text = "Train A.I model",
                icon = Icons.Filled.Science,
                color = Color.Green,
                onClick = { navController.navigate("train_ai") }
            )
            ButtonWithIcon(
                text = "Import A.I model",
                icon = Icons.Filled.Download,
                color = Color.Cyan,
                onClick = { navController.navigate("import_model") }
            )
            ButtonWithIcon(
                text = "Settings",
                icon = Icons.Filled.Settings,
                color = Color.Yellow,
                onClick = { navController.navigate("settings") }
            )
        }
    }
}

@Composable
fun ButtonWithIcon(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.Black)
        }
    }
}