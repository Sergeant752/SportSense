package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.ChatDao
import mobappdev.example.sportsense.data.ChatMessage
import mobappdev.example.sportsense.data.SensorDatabase

class ChatVM(application: Application) : AndroidViewModel(application) {
    private val chatDao: ChatDao = SensorDatabase.getDatabase(application).chatDao()

    val messages: LiveData<List<ChatMessage>> = chatDao.getAllMessages().asLiveData()

    fun sendMessage(sender: String, message: String) {
        if (message.isBlank()) return
        val chatMessage = ChatMessage(sender = sender, message = message, timestamp = System.currentTimeMillis())
        viewModelScope.launch {
            chatDao.insertMessage(chatMessage)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatDao.clearChat()
        }
    }
}
