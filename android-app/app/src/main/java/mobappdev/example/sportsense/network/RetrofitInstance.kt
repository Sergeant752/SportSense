package mobappdev.example.sportsense.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://0.0.0.0:5000/")  // Ändra till din Python-server-IP
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}
