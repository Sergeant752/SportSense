package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel

@Composable
fun UserListScreen(navController: NavController, userViewModel: UserViewModel, currentUser: String) {
    val context = LocalContext.current
    val registeredUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF311B92), Color(0xFF1B1F3B))
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Select a user to chat with",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(registeredUsers) { user ->
                if (user.username != currentUser) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("chat/$currentUser/${user.username}")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF42A5F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    navController.navigate("chat/$currentUser/${user.username}")
                                }
                            ) {
                                Icon(
                                    Icons.Default.Chat,
                                    contentDescription = "Chat",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}