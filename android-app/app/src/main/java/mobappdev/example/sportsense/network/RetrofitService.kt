package mobappdev.example.sportsense.network

import mobappdev.example.sportsense.data.SensorData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("/analyze")
    suspend fun analyzeData(@Body data: List<SensorData>): Response<List<SensorData>>

}
