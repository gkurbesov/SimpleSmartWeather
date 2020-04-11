package ru.gennady.kurbesov.simplesmartweather.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gennady.kurbesov.simplesmartweather.model.CitySearchResponse


interface MapsSputnikApiService {
    @GET("search")
    fun search(@Query("q") query: String): Call<CitySearchResponse>?

    companion object {
        private const val  BASE_URL = "http://search.maps.sputnik.ru/"
        @Volatile private var retorfit: Retrofit? = null
        private val LOCK = Any()

        fun create(): MapsSputnikApiService {
            return retorfit?.create(MapsSputnikApiService::class.java) ?: synchronized(LOCK) {
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
                return retorfit!!.create(MapsSputnikApiService::class.java)
            }
        }
    }
}