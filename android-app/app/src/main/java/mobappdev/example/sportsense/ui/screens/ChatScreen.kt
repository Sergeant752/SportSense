package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.sportsense.data.ChatMessage
import mobappdev.example.sportsense.ui.viewmodels.ChatVM
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    chatVM: ChatVM,
    userViewModel: UserViewModel,
    navController: NavController,
    currentUser: String,
    recipient: String
) {
    val messages by chatVM.getMessagesForChat(currentUser, recipient).observeAsState(emptyList())
    val context = LocalContext.current
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var clearChatMenuExpanded by remember { mutableStateOf(false) }

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
        // 🔹 Top bar with recipient's name and clear chat option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chat with $recipient",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(onClick = { clearChatMenuExpanded = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = Color.Red)
            }
        }

        // 🔹 Messages List
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageItem(message, currentUser)
            }
        }

        // 🔹 Message Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(8.dp)
            )
            IconButton(
                onClick = {
                    if (text.text.isNotEmpty()) {
                        chatVM.sendMessage(currentUser, recipient, text.text)
                        text = TextFieldValue("")
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Green)
            }
        }

        // 🔹 Clear Chat Dropdown Menu
        DropdownMenu(
            expanded = clearChatMenuExpanded,
            onDismissRequest = { clearChatMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Clear Today") },
                onClick = {
                    chatVM.clearChat(currentUser, recipient, "today")
                    clearChatMenuExpanded = false
                    Toast.makeText(context, "Today's chat cleared!", Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text("Clear Week") },
                onClick = {
                    chatVM.clearChat(currentUser, recipient, "week")
                    clearChatMenuExpanded = false
                    Toast.makeText(context, "Week's chat cleared!", Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text("Clear All") },
                onClick = {
                    chatVM.clearChat(currentUser, recipient, "all")
                    clearChatMenuExpanded = false
                    Toast.makeText(context, "All chat cleared!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

// 🔹 Updated Chat Message UI
@Composable
fun ChatMessageItem(message: ChatMessage, currentUser: String) {
    val isSentByCurrentUser = message.sender == currentUser
    val backgroundColor = if (isSentByCurrentUser) Color(0xFF1976D2) else Color(0xFF42A5F5)
    val alignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .widthIn(max = 280.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isSentByCurrentUser) "You" else message.sender,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// 🔹 Function to format timestamp
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}