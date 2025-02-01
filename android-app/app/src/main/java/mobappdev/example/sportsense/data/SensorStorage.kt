package mobappdev.example.sportsense.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SensorStorage {
    private const val PREFS_NAME = "SensorPrefs"
    private const val KEY_SENSOR_HISTORY = "sensor_history"

    fun saveSensorData(context: Context, data: SensorData) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historyJson = prefs.getString(KEY_SENSOR_HISTORY, "[]")
        val type = object : TypeToken<MutableList<SensorData>>() {}.type

        val history: MutableList<SensorData> = Gson().fromJson(historyJson, type)
        history.add(data)

        prefs.edit().putString(KEY_SENSOR_HISTORY, Gson().toJson(history)).apply()
    }

    fun getSensorHistory(context: Context): List<SensorData> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SENSOR_HISTORY, "[]")
        val type = object : TypeToken<List<SensorData>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearHistory(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_SENSOR_HISTORY).apply()
    }
}
