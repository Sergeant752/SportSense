package mobappdev.example.sportsense.ui.screens

import android.content.Context
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
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.ui.viewmodels.SensorVM

@Composable
fun SettingsScreen(vm: SensorVM) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Hanterar coroutines

    var isRealTimeUpdateEnabled by remember { mutableStateOf(true) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var autoConnectEnabled by remember { mutableStateOf(false) }
    var autoClearHistoryEnabled by remember { mutableStateOf(false) }

    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1),  // Djup blå
                        Color(0xFF311B92),  // Mörk lila-blå
                        Color(0xFF1B1F3B)   // Nästan svart
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

                // ✅ Uppdaterad Backup-knapp
                Button(
                    onClick = {
                        coroutineScope.launch { // Starta coroutine
                            val data = vm.getAllSensorData()  // Hämta data via ViewModel
                            val filePath = SensorStorage.exportSensorDataAsJSON(context, data)
                            Toast.makeText(context, "Backup created at $filePath", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Backup, contentDescription = "Backup", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backup sensor data", color = Color.White)
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