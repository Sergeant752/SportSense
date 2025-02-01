package mobappdev.example.sportsense.data

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter

object SensorStorage {
    private const val PREFS_NAME = "SensorPrefs"
    private const val KEY_SENSOR_HISTORY = "sensor_history"

    fun saveSensorData(context: Context, data: SensorData) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historyJson = prefs.getString(KEY_SENSOR_HISTORY, "[]")
        val type = object : TypeToken<MutableList<SensorData>>() {}.type

        val history: MutableList<SensorData> = Gson().fromJson(historyJson, type)
        history.add(data)

        prefs.edit().putString(KEY_SENSOR_HISTORY, Gson().toJson(history)).apply()
    }

    fun saveSensorDataWithTag(context: Context, data: SensorData, tag: String) {
        val updatedData = data.copy(tag = tag)
        saveSensorData(context, updatedData)
    }

    fun getSensorHistory(context: Context): List<SensorData> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SENSOR_HISTORY, "[]")
        val type = object : TypeToken<List<SensorData>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()  // ✅ Använder clear() istället för remove() för att rensa allt
    }

    fun exportSensorDataAsCSV(context: Context): String {
        val data = getSensorHistory(context)
        val fileName = "sensor_data_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            writer.append("timestamp,heartRate,accelX,accelY,accelZ,gyroX,gyroY,gyroZ,tag\n")
            data.forEach {
                writer.append("${it.timestamp},${it.heartRate},${it.accelX},${it.accelY},${it.accelZ},${it.gyroX},${it.gyroY},${it.gyroZ},${it.tag ?: ""}\n")
            }
        }
        return file.absolutePath
    }

    fun exportSensorDataAsJSON(context: Context): String {
        val data = getSensorHistory(context)
        val json = Gson().toJson(data)
        val fileName = "sensor_data_${System.currentTimeMillis()}.json"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        file.writeText(json)
        return file.absolutePath
    }
}