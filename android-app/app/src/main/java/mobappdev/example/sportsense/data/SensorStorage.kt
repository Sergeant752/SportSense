package mobappdev.example.sportsense.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object SensorStorage {
    private const val PREFS_NAME = "SensorPrefs"
    private const val KEY_SENSOR_DATA = "saved_sensor_data"

    fun saveSensorData(context: Context, data: SensorData) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SENSOR_DATA, Gson().toJson(data)).apply()
    }

    fun getSavedSensorData(context: Context): SensorData? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SENSOR_DATA, null) ?: return null
        return Gson().fromJson(json, SensorData::class.java)
    }
}
