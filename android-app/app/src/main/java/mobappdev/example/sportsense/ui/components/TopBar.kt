package mobappdev.example.sportsense.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Favorite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    var showBar by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(Color(0xFF0D47A1)) }
    LaunchedEffect(Unit) {
        delay(300)
        showBar = true
    }
    LaunchedEffect(Unit) {
        while (true) {
            backgroundColor = getNextBackgroundColor(backgroundColor)
            delay(2000)
        }
    }
    if (showBar) {
        CenterAlignedTopAppBar(
            modifier = Modifier.shadow(6.dp),
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Pulse Icon",
                        tint = Color.Red
                    )
                }
            },
            title = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    CurrentTime()
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.SportsBasketball,
                        contentDescription = "Basket Icon",
                        tint = Color.Yellow
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor
            )
        )
    }
}

fun getNextBackgroundColor(currentColor: Color): Color {
    val colors = listOf(
        Color(0xFF0D47A1),
        Color(0xFF311B92),
        Color(0xFF001F3F)
    )
    return colors[(colors.indexOf(currentColor) + 1) % colors.size]
}

@Composable
fun CurrentTime() {
    var time by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }
    Text(
        text = time,
        color = Color.White,
        textAlign = TextAlign.End
    )
}