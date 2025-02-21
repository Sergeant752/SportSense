package mobappdev.example.sportsense.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SensorData::class, User::class, ChatMessage::class], version = 8, exportSchema = true)
abstract class SensorDatabase : RoomDatabase() {
    abstract fun sensorDao(): SensorDao
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: SensorDatabase? = null

        fun getDatabase(context: Context): SensorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SensorDatabase::class.java,
                    "sensor_database"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user ADD COLUMN last_active INTEGER DEFAULT 0 NOT NULL")
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN message_status TEXT DEFAULT 'sent' NOT NULL")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN is_read INTEGER DEFAULT 0 NOT NULL")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Förbered för framtida ändringar, ingen ändring nu
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 🔹 Skapa en ny kolumn `chat_id` för att identifiera separata chattar
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN chat_id TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}