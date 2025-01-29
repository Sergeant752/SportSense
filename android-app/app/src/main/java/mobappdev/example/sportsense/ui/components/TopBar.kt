package mobappdev.example.sportsense.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Favorite // Pulsikon (hjärtslag)
import androidx.compose.ui.graphics.Color
import mobappdev.example.sportsense.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    CenterAlignedTopAppBar(
        navigationIcon = { // Lägger pulsikon till vänster
            IconButton(onClick = { /* Lägg till en funktion om du vill */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Pulse Icon",
                    tint = Color.Red // Röd färg för puls
                )
            }
        },
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                color = Color.Blue // Ljusblå text
            )
        },
        actions = { // Lägger basket-ikonen längst till höger
            IconButton(onClick = { /* Lägg till en funktion om du vill */ }) {
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