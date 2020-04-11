package ru.gennady.kurbesov.simplesmartweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TimingLogger
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Response
import ru.gennady.kurbesov.simplesmartweather.model.CitySearchResponse
import ru.gennady.kurbesov.simplesmartweather.model.WeatherInfo
import ru.gennady.kurbesov.simplesmartweather.model.WeatherResponse
import ru.gennady.kurbesov.simplesmartweather.network.MapsSputnikApiService
import ru.gennady.kurbesov.simplesmartweather.network.OpenweathermapApiService

class MainActivity : AppCompatActivity() {

    var lbTemp: TextView? = null
    var lbTempLike: TextView? = null
    var lbCity: TextView? = null
    var lbWindly: TextView? = null
    var lbCloud: TextView? = null
    var lbWeather: TextView? = null

    var swipeRefresher: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lbTemp = findViewById(R.id.lb_temp)
        lbTempLike = findViewById(R.id.lb_temp_like)
        lbCity = findViewById(R.id.lb_city)
        lbWindly = findViewById(R.id.lb_windly)
        lbCloud = findViewById(R.id.lb_cloud)
        lbWeather = findViewById(R.id.lb_weather)

        swipeRefresher = findViewById(R.id.swipe_refresher)
        swipeRefresher?.setColorSchemeColors(getColor(R.color.colorAccent))
        swipeRefresher?.setOnRefreshListener {
            Log.e("SWIPE", "call to refresh")
            update()
            Handler().postDelayed({ cancleSwipeUpdate() }, 5000)
        }
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    fun update() {
        val res = MapsSputnikApiService.create().search("москва")
        res?.enqueue(object : retrofit2.Callback<CitySearchResponse> {
            override fun onFailure(call: Call<CitySearchResponse>, t: Throwable) {
                Log.e("SEARCH CITY", t.message)
                cancleSwipeUpdate()
            }

            override fun onResponse(
                call: Call<CitySearchResponse>,
                response: Response<CitySearchResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.result?.forEach {
                        Log.d(
                            "SEARCH CITY",
                            "city ${it.description}, lat: ${it.position?.lat} lon: ${it.position?.lon}"
                        )
                        setCityName(it.description)
                        getWeather(it.position?.lat, it.position?.lon)
                    }
                }
            }
        })
    }

    fun cancleSwipeUpdate() {
        runOnUiThread{
            swipeRefresher?.isRefreshing = false
        }
    }

    fun getWeather(lat: Double, lon: Double) {
        val res = OpenweathermapApiService.create().currentWeather(lat, lon)
        res?.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("GET WEATHER", t.message)
                cancleSwipeUpdate()
            }

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    response?.body()?.current.let {
                        Log.e(
                            "GET WEATHER",
                            "Температура: ${it?.temp}, ощущается как ${it?.feels_like} - ${it?.weather!![0]?.description ?: ""}"
                        )
                        setWeatherData(it)
                    }
                }
                cancleSwipeUpdate()
            }
        })
    }

    fun setCityName(value: String) {
        runOnUiThread {
            lbCity?.text = value
        }
    }

    fun setWeatherData(weather: WeatherInfo) {
        runOnUiThread {
            lbTemp?.text = "${weather.temp} \u2103"
            lbTempLike?.text =
                "Ощущается как ${weather.feels_like} \u2103"
            lbWindly?.text = "${weather.wind_speed} м/с"
            lbCloud?.text = "${weather.clouds} %"
            lbWeather?.text =
                "${weather?.weather!![0]?.description ?: ""}"
        }
    }
}
