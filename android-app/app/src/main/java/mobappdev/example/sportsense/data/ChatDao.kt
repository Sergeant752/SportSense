package mobappdev.example.sportsense.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE sender = :username OR recipient = :username ORDER BY timestamp DESC")
    fun getMessagesForUser(username: String): Flow<List<ChatMessage>>

    @Query("DELETE FROM chat_messages WHERE sender = :username OR recipient = :username")
    suspend fun clearChatForUser(username: String)
}