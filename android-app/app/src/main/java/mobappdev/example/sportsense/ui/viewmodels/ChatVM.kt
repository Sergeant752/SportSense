package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.ChatDao
import mobappdev.example.sportsense.data.ChatMessage
import mobappdev.example.sportsense.data.SensorDatabase

class ChatVM(application: Application) : AndroidViewModel(application) {
    private val chatDao: ChatDao = SensorDatabase.getDatabase(application).chatDao()

    fun getMessagesForUser(username: String) = chatDao.getMessagesForUser(username).asLiveData()

    fun sendMessage(sender: String, recipient: String, message: String) {
        val chatMessage = ChatMessage(sender = sender, recipient = recipient, message = message, timestamp = System.currentTimeMillis())
        viewModelScope.launch {
            chatDao.insertMessage(chatMessage)
        }
    }

    fun clearChatForUser(username: String) {
        viewModelScope.launch {
            chatDao.clearChatForUser(username)
        }
    }
}