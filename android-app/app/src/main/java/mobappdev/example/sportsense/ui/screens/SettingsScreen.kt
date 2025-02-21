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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.ui.viewmodels.SensorVM
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel

@Composable
fun SettingsScreen(navController: NavController,vm: SensorVM, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    var isRealTimeUpdateEnabled by remember { mutableStateOf(true) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var autoConnectEnabled by remember { mutableStateOf(false) }
    var autoClearHistoryEnabled by remember { mutableStateOf(false) }

    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
    )

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            Toast.makeText(context, "Sign in/Register to access this page", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        }
    }
    if (!isLoggedIn) return

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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.Yellow),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(cardGradient)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                SettingItem(
                    title = "Real-time updates",
                    icon = Icons.Default.Refresh,
                    iconColor = Color(0xFF4CAF50),
                    checked = isRealTimeUpdateEnabled,
                    onCheckedChange = { isRealTimeUpdateEnabled = it }
                )
                SettingItem(
                    title = "Enable notifications",
                    icon = Icons.Default.Notifications,
                    iconColor = Color(0xFFFFC107),
                    checked = true,
                    onCheckedChange = { /* Implementera notifikationslogik här */ }
                )
                SettingItem(
                    title = "Dark mode",
                    icon = Icons.Default.BrightnessMedium,
                    iconColor = Color(0xFF9C27B0),
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isDarkModeEnabled = it }
                )
                SettingItem(
                    title = "Auto-connect to last device",
                    icon = Icons.Default.BluetoothConnected,
                    iconColor = Color.Blue,
                    checked = autoConnectEnabled,
                    onCheckedChange = { autoConnectEnabled = it }
                )
                SettingItem(
                    title = "Auto-clear history on exit",
                    icon = Icons.Default.DeleteSweep,
                    iconColor = Color.Red,
                    checked = autoClearHistoryEnabled,
                    onCheckedChange = { autoClearHistoryEnabled = it }
                )
                Button(
                    onClick = {
                        userViewModel.logoutUser()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.Black)
                }
            }
        }
    }
}



@Composable
fun SettingItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color.Red
            )
        )
    }
}