package mobappdev.example.sportsense.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE sender = :username OR recipient = :username ORDER BY timestamp DESC")
    fun getMessagesForUser(username: String): Flow<List<ChatMessage>>

    @Query("DELETE FROM chat_messages WHERE (sender = :username OR recipient = :username) AND timestamp >= :timeLimit")
    suspend fun clearChatForUser(username: String, timeLimit: Long)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE recipient = :username AND is_read = 0")
    fun getUnreadMessageCount(username: String): Flow<Int>

    @Query("UPDATE chat_messages SET is_Read = 1 WHERE recipient = :username")
    suspend fun markMessagesAsRead(username: String)
}
