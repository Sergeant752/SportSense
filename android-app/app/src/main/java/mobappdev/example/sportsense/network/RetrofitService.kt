package mobappdev.example.sportsense.network

import mobappdev.example.sportsense.data.SensorData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {
    @POST("/analyze")
    suspend fun analyzeData(@Body data: List<SensorData>): Response<List<SensorData>>

    @GET("/get-analysis")
    suspend fun getAnalysis(): Response<List<SensorData>>

    @GET("/get-model")
    suspend fun downloadModel(): Call<ResponseBody>
}
