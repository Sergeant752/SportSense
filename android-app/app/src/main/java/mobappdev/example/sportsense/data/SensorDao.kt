package mobappdev.example.sportsense.data

import androidx.room.*

@Dao
interface SensorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorData(data: SensorData)

    @Query("SELECT * FROM sensor_data ORDER BY timestamp DESC")
    suspend fun getAllSensorData(): List<SensorData>

    @Query("DELETE FROM sensor_data")
    suspend fun clearSensorData()

    @Delete
    suspend fun deleteSensorData(data: SensorData)
}
