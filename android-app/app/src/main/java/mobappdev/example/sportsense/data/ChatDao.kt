package mobappdev.example.sportsense.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE chat_id = :chatId ORDER BY timestamp DESC")
    fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>>

    @Query("DELETE FROM chat_messages WHERE chat_id = :chatId AND timestamp >= :timeLimit")
    suspend fun clearChat(chatId: String, timeLimit: Long)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE recipient = :username AND is_read = 0")
    fun getUnreadMessageCount(username: String): Flow<Int>

    @Query("UPDATE chat_messages SET is_read = 1 WHERE recipient = :username AND is_read = 0")
    suspend fun markMessagesAsRead(username: String)
}
