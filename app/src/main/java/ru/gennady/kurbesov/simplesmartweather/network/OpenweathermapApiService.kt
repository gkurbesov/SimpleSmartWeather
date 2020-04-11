package ru.gennady.kurbesov.simplesmartweather.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gennady.kurbesov.simplesmartweather.model.WeatherResponse

interface OpenweathermapApiService {
    @GET("onecall")
    fun currentWeather(@Query("lat") lat: Double,
                       @Query("lon") lon: Double,
                       @Query("appid") appid: String = API_KEY,
                       @Query("units") units: String = UNIT_CELSIUS,
                       @Query("lang") lang: String = LANG_RU
                       ): Call<WeatherResponse>?

    companion object {
        const val UNIT_FAHRENHEIT = "imperial"
        const val UNIT_CELSIUS = "metric"
        const val LANG_RU = "ru"
        private const val API_KEY = "41991f8d8e3702431c5e839102b5b281"
        private const val  BASE_URL = "https://api.openweathermap.org/data/2.5/"
        @Volatile private var retorfit: Retrofit? = null
        private val LOCK = Any()

        fun create(): OpenweathermapApiService {
            return retorfit?.create(OpenweathermapApiService::class.java) ?: synchronized(LOCK) {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                val client = OkHttpClient.Builder()
                client.addInterceptor(logging)

                retorfit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.build())
                    .build()
                return retorfit!!.create(OpenweathermapApiService::class.java)
            }
        }
    }
}