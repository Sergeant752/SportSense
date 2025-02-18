package mobappdev.example.sportsense.data

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object SensorStorage {

    private fun getDatabase(context: Context) = SensorDatabase.getDatabase(context)
    private fun getDao(context: Context) = getDatabase(context).sensorDao()

    suspend fun saveSensorData(context: Context, data: SensorData) {
        withContext(Dispatchers.IO) {
            getDao(context).insertSensorData(data)
        }
    }

    suspend fun getSensorHistory(context: Context): List<SensorData> {
        return withContext(Dispatchers.IO) {
            getDao(context).getAllSensorData()
        }
    }

    suspend fun clearHistory(context: Context) {
        withContext(Dispatchers.IO) {
            getDao(context).clearSensorData()
        }
    }

    suspend fun deleteSensorData(context: Context, data: SensorData) {
        withContext(Dispatchers.IO) {
            getDao(context).deleteSensorData(data)
        }
    }

    fun exportSensorDataAsCSV(context: Context, data: List<SensorData>): String {
        val fileName = "sensor_data_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        file.printWriter().use { writer ->
            writer.println("timestamp,heartRate,accelX,accelY,accelZ,gyroX,gyroY,gyroZ,tag")
            data.forEach {
                writer.println("${it.timestamp},${it.heartRate},${it.accelX},${it.accelY},${it.accelZ},${it.gyroX},${it.gyroY},${it.gyroZ},${it.movementDetected ?: ""}")
            }
        }
        return file.absolutePath
    }

    fun exportSensorDataAsJSON(context: Context, data: List<SensorData>): String {
        val json = Gson().toJson(data)
        val fileName = "sensor_data_${System.currentTimeMillis()}.json"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        file.writeText(json)
        return file.absolutePath
    }

}