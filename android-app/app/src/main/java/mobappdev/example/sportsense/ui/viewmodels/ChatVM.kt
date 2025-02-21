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
    private val appContext: Context = application.applicationContext

    fun getMessagesForChat(user1: String, user2: String): LiveData<List<ChatMessage>> {
        val chatId = listOf(user1, user2).sorted().joinToString("_")
        return chatDao.getMessagesForChat(chatId).asLiveData()
    }

    fun sendMessage(sender: String, recipient: String, message: String) {
        val chatId = listOf(sender, recipient).sorted().joinToString("_")

        val chatMessage = ChatMessage(
            sender = sender,
            recipient = recipient,
            message = message,
            timestamp = System.currentTimeMillis(),
            chat_id = chatId,
            isRead = 0
        )

        viewModelScope.launch {
            chatDao.insertMessage(chatMessage)
            NotificationHelper.sendNotification(appContext, "New message from $sender", message)
        }
    }

    fun getUnreadMessageCount(username: String): LiveData<Int> {
        return chatDao.getUnreadMessageCount(username).asLiveData()
    }

    fun markMessagesAsRead(username: String) {
        viewModelScope.launch {
            chatDao.markMessagesAsRead(username)
        }
    }

    fun clearChat(user1: String, user2: String, period: String) {
        val chatId = listOf(user1, user2).sorted().joinToString("_")
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val timeLimit = when (period) {
                "today" -> currentTime - 24 * 60 * 60 * 1000
                "week" -> currentTime - 7 * 24 * 60 * 60 * 1000
                "all" -> 0L
                else -> return@launch
            }
            chatDao.clearChat(chatId, timeLimit)
        }
    }
}