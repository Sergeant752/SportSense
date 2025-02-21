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
    var clearChatMenuExpanded by remember { mutableStateOf(false) }

    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
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
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF311B92), Color(0xFF1B1F3B))
                )
            )
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Chat", color = Color.White, style = MaterialTheme.typography.headlineSmall)

            Row {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text(text = selectedRecipient ?: "Send To", color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        registeredUsers.filter { it.username != username }.forEach { user ->
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

                IconButton(onClick = { clearChatMenuExpanded = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = Color.Red)
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

        DropdownMenu(
            expanded = clearChatMenuExpanded,
            onDismissRequest = { clearChatMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Clear Today") },
                onClick = {
                    chatVM.clearChatForUser(username, "today")
                    clearChatMenuExpanded = false
                    Toast.makeText(context, "Today's chat cleared!", Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text("Clear Week") },
                onClick = {
                    chatVM.clearChatForUser(username, "week")
                    clearChatMenuExpanded = false
                    Toast.makeText(context, "Week's chat cleared!", Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text("Clear All") },
                onClick = {
                    chatVM.clearChatForUser(username, "all")
                    clearChatMenuExpanded = false
                    Toast.makeText(context, "All chat cleared!", Toast.LENGTH_SHORT).show()
                }
            )
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