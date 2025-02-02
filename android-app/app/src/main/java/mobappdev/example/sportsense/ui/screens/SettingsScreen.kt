package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var isRealTimeUpdateEnabled by remember { mutableStateOf(true) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }

    // Blå gradient som i HistoryScreen
    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5)) // Blå gradient
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // 🖤 Svart bakgrund
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.Yellow), // 🟡 Gul rubrik
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(cardGradient) // 🔵 Blå gradient för kortet
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                SettingItem(
                    title = "Real-Time Updates",
                    icon = Icons.Default.Refresh,
                    iconColor = Color(0xFF4CAF50), // ✅ Grön ikon
                    checked = isRealTimeUpdateEnabled,
                    onCheckedChange = { isRealTimeUpdateEnabled = it }
                )

                SettingItem(
                    title = "Enable Notifications",
                    icon = Icons.Default.Notifications,
                    iconColor = Color(0xFFFFC107), // 🌟 Gul ikon
                    checked = true,
                    onCheckedChange = { /* Implementera notifikationslogik här */ }
                )

                SettingItem(
                    title = "Dark Mode",
                    icon = Icons.Default.BrightnessMedium,
                    iconColor = Color(0xFF9C27B0), // 💜 Lila ikon
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isDarkModeEnabled = it }
                )
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
            tint = iconColor, // 🎨 Färgad ikon
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White, // Vit text för bättre kontrast på blå bakgrund
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF4CAF50), // ✅ Grön switch när aktiverad
                uncheckedThumbColor = Color.Red
            )
        )
    }
}