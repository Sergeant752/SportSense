package mobappdev.example.sportsense.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import mobappdev.example.sportsense.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    var showBar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Vänta lite innan fade-in
        showBar = true
    }

    if (showBar) {
        CenterAlignedTopAppBar(
            modifier = Modifier.shadow(6.dp), // Skugga på TopBar
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
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    color = Color.Blue
                )
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
                containerColor = DarkBlue
            )
        )
    }
}