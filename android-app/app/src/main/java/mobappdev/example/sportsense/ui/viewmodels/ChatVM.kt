package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.ChatDao
import mobappdev.example.sportsense.data.ChatMessage
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.notifications.NotificationHelper

class ChatVM(application: Application) : AndroidViewModel(application) {
    private val chatDao: ChatDao = SensorDatabase.getDatabase(application).chatDao()
    private val _unreadMessageCount = chatDao.getUnreadMessageCount("").asLiveData()
    private val appContext: Context = application.applicationContext

    fun getMessagesForUser(username: String): LiveData<List<ChatMessage>> {
        return chatDao.getMessagesForUser(username).asLiveData()
    }

    fun sendMessage(sender: String, recipient: String, message: String) {
        val chatMessage = ChatMessage(
            sender = sender,
            recipient = recipient,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = 0
        )
        viewModelScope.launch {
            chatDao.insertMessage(chatMessage)
            updateUnreadMessageCount(recipient)
            NotificationHelper.sendNotification(appContext, "New message from $sender", message)
        }
    }

    fun getUnreadMessageCount(username: String): LiveData<Int> {
        return chatDao.getUnreadMessageCount(username).asLiveData()
    }

    private fun updateUnreadMessageCount(username: String) {
        viewModelScope.launch {
            chatDao.getUnreadMessageCount(username)
        }
    }

    fun markMessagesAsRead(username: String) {
        viewModelScope.launch {
            chatDao.markMessagesAsRead(username)
            updateUnreadMessageCount(username)
        }
    }

    fun clearChatForUser(username: String, period: String) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val timeLimit = when (period) {
                "today" -> currentTime - 24 * 60 * 60 * 1000
                "week" -> currentTime - 7 * 24 * 60 * 60 * 1000
                "all" -> 0L
                else -> return@launch
            }
            chatDao.clearChatForUser(username, timeLimit)
            updateUnreadMessageCount(username)
        }
    }
}