package mobappdev.example.sportsense.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.utils.Result
import java.net.HttpURLConnection
import java.net.URL

object SensorDataSource {
    private const val BASE_URL = "https://example.com/api/sensordata" // Uppdatera med API-URL

    suspend fun fetchSensorData(): Result<SensorData> {
        val url = URL(BASE_URL)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val json = inputStream.bufferedReader().use { it.readText() }

                // Deserialisera JSON till SensorData-objekt
                val data = Gson().fromJson(json, SensorData::class.java)

                Result.Success(data)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
