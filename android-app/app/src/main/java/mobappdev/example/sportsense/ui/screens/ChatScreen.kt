package mobappdev.example.sportsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
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
import mobappdev.example.sportsense.ui.viewmodels.UserViewModel

@Composable
fun ChatScreen(chatVM: ChatVM, userViewModel: UserViewModel, navController: NavController, username: String) {
    val messages by chatVM.getMessagesForUser(username).observeAsState(emptyList())
    val context = LocalContext.current

    val registeredUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var selectedRecipient by remember { mutableStateOf<String?>(null) }

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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Chat", color = Color.White, style = MaterialTheme.typography.headlineSmall)
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = selectedRecipient ?: "Send To", color = Color.White)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    registeredUsers.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.username) },
                            onClick = {
                                selectedRecipient = user.username
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
                    if (!selectedRecipient.isNullOrEmpty() && text.text.isNotEmpty()) {
                        chatVM.sendMessage(username, selectedRecipient!!, text.text)
                        text = TextFieldValue("")
                    }
                },
                enabled = !selectedRecipient.isNullOrEmpty()
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (selectedRecipient.isNullOrEmpty()) Color.Gray else Color.Green
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                chatVM.clearChatForUser(username)
                Toast.makeText(context, "Chat history cleared!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear Chat")
        }
    }
}

@Composable
fun ChatMessageItem(message: mobappdev.example.sportsense.data.ChatMessage) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "${message.sender} → ${message.recipient}", color = Color.Cyan, style = MaterialTheme.typography.bodySmall)
        Text(text = message.message, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}