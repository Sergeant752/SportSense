package mobappdev.example.sportsense.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }

        val fileName = "sensor_data_${System.currentTimeMillis()}.csv"
        val file = File(directory, fileName)

        return try {
            FileOutputStream(file).use { fos ->
                fos.write("timestamp,heartRate,accelX,accelY,accelZ,gyroX,gyroY,gyroZ,tag\n".toByteArray())
                data.forEach {
                    fos.write("${it.timestamp},${it.heartRate},${it.accelX},${it.accelY},${it.accelZ},${it.gyroX},${it.gyroY},${it.gyroZ},${it.movementDetected ?: ""}\n".toByteArray())
                }
            }
            Log.d("SensorStorage", "CSV exported successfully: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e("SensorStorage", "Error exporting CSV: ${e.message}")
            "Error exporting file"
        }
    }

    fun exportSensorDataAsJSON(context: Context, data: List<SensorData>): String {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }

        val fileName = "sensor_data_${System.currentTimeMillis()}.json"
        val file = File(directory, fileName)

        return try {
            val json = Gson().toJson(data)
            file.writeText(json)
            Log.d("SensorStorage", "JSON exported successfully: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e("SensorStorage", "Error exporting JSON: ${e.message}")
            "Error exporting file"
        }
    }
}