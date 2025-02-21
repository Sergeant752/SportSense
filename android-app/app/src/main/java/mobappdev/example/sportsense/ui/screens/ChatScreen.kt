package mobappdev.example.sportsense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.ui.viewmodels.ChatVM

@Composable
fun ChatScreen(vm: ChatVM, navController: NavController, username: String) {
    val messages by vm.messages.observeAsState(emptyList())
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

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
        Text("Chat", color = Color.White, style = MaterialTheme.typography.headlineSmall)

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, MaterialTheme.shapes.small)
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    if (text.text.isNotEmpty()) {
                        vm.sendMessage(username, text.text)
                        text = TextFieldValue("")
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: mobappdev.example.sportsense.data.ChatMessage) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = message.sender, color = Color.Cyan, style = MaterialTheme.typography.bodySmall)
        Text(text = message.message, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}