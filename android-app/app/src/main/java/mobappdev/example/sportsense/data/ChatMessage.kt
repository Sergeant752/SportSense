package mobappdev.example.sportsense.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,
    val recipient: String,
    val message: String,
    val timestamp: Long,
    val chat_id: String,
    @ColumnInfo(name = "is_read") val isRead: Int = 0
)