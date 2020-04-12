package ru.gennady.kurbesov.simplesmartweather.weather

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import retrofit2.Call
import retrofit2.Response
import ru.gennady.kurbesov.simplesmartweather.R
import ru.gennady.kurbesov.simplesmartweather.model.*
import ru.gennady.kurbesov.simplesmartweather.network.MapsSputnikApiService
import ru.gennady.kurbesov.simplesmartweather.network.OpenweathermapApiService
import java.util.*

class WeatherClient(val activity: Activity, val listener: IWeatherClientListener?) {

    fun searchCity(name: String) {
        val res = MapsSputnikApiService.create().search(name)
        res?.enqueue(object : retrofit2.Callback<CitySearchResponse> {
            override fun onFailure(call: Call<CitySearchResponse>, t: Throwable) {
                Log.e("SEARCH CITY", t.message)
                callOnError()
            }

            override fun onResponse(
                call: Call<CitySearchResponse>,
                response: Response<CitySearchResponse>
            ) {
                if (response.isSuccessful) {
                    if(response.body()?.result != null)
                        callOnCityList(response.body()?.result!!.toList())
                }
            }
        })
    }

    fun update() {
        val res = MapsSputnikApiService.create().search(getCityName(activity))
        res?.enqueue(object : retrofit2.Callback<CitySearchResponse> {
            override fun onFailure(call: Call<CitySearchResponse>, t: Throwable) {
                Log.e("SEARCH CITY", t.message)
                callOnError()
            }

            override fun onResponse(
                call: Call<CitySearchResponse>,
                response: Response<CitySearchResponse>
            ) {
                if (response.isSuccessful && response.body()?.result != null && response.body()?.result!!.size > 0) {
                    response.body()?.result!![0].let {
                        Log.d(
                            "SEARCH CITY",
                            "city ${it.description}, lat: ${it.position?.lat} lon: ${it.position?.lon}"
                        )
                        //setLocations(activity, it.position.lat, it.position.lon)
                        callOnCity(it.description)
                        getWeather(it.position?.lat, it.position?.lon)
                    }
                }
            }
        })
    }

    fun getWeather(lat: Double, lon: Double) {
        val res = OpenweathermapApiService.create().currentWeather(lat, lon)
        res?.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("GET WEATHER", t.message)
                callOnError()
            }

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.current.let {
                        Log.e(
                            "GET WEATHER",
                            "Температура: ${it?.temp}, ощущается как ${it?.feels_like} - ${it?.weather!![0]?.description ?: ""}"
                        )
                        val weather = buildWeatherData(it)
                        callOnWeather(weather)
                        val weatherHint = SmartWeatherHint.check(weather.temp, weather.wind, weather.cloud, weather.rain)
                        callOnWeatherHint(weatherHint)
                    }
                } else {
                    callOnError()
                }

            }
        })
    }

    private fun buildWeatherData(value: WeatherInfo): WeatherData {
        val rain =
            if (value.weather != null && value.weather!!.size >= 1) getRainFlag(value = value.weather!![0]) else false
        val weather_desc = getWeatherDescription(value)
        return WeatherData(
            value.temp,
            value.feels_like,
            value.pressure,
            value.humidity,
            value.clouds,
            value.wind_speed,
            rain,
            weather_desc
        )
    }

    private fun getRainFlag(value: WeatherState): Boolean {
        return when (value.id) {
            in 200..531 -> {
                true
            }
            else -> false
        }
    }

    private fun getWeatherDescription(value: WeatherInfo): String {
        return if (value.weather != null && value.weather!!.size > 0) {
            value.weather!![0].description ?: activity.getString(R.string.empty_data)
        } else {
            activity.getString(R.string.empty_data)
        }
    }

    private fun callOnError() {
        activity.runOnUiThread { listener?.onError() }
    }

    private fun callOnCity(value: String) {
        activity.runOnUiThread { listener?.onCity(value) }
    }

    private fun callOnWeather(data: WeatherData) {
        activity.runOnUiThread { listener?.onWeatherReceive(data) }
    }

    private fun callOnWeatherHint(value: String) {
        activity.runOnUiThread { listener?.onWeatherHint(value) }
    }

    private fun callOnCityList(value: List<CityInfo>){
        activity.runOnUiThread { listener?.onCityList(value) }
    }

    companion object {

        private const val PREF_NAME = "weather_setting"

        fun setLocations(context: Context, lat: Double = 0.0, lon: Double = 0.0) {
            val settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            settings.putFloat("LOCATION_LAT", lat.toFloat()).apply()
            settings.putFloat("LOCATION_LON", lon.toFloat()).apply()
        }

        fun setCity(context: Context, city: String?) {
            val settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            settings.putString("CITY_NAME", city).apply()
        }

        fun getCityName(context: Context): String {
            val settings: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return settings.getString("CITY_NAME", null) ?: "Москва"
        }
    }

}