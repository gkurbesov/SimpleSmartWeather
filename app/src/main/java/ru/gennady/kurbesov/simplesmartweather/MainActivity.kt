package ru.gennady.kurbesov.simplesmartweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.util.TimingLogger
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Response
import ru.gennady.kurbesov.simplesmartweather.model.CityInfo
import ru.gennady.kurbesov.simplesmartweather.model.CitySearchResponse
import ru.gennady.kurbesov.simplesmartweather.model.WeatherInfo
import ru.gennady.kurbesov.simplesmartweather.model.WeatherResponse
import ru.gennady.kurbesov.simplesmartweather.network.MapsSputnikApiService
import ru.gennady.kurbesov.simplesmartweather.network.OpenweathermapApiService
import ru.gennady.kurbesov.simplesmartweather.weather.IWeatherClientListener
import ru.gennady.kurbesov.simplesmartweather.weather.WeatherClient
import ru.gennady.kurbesov.simplesmartweather.weather.WeatherData

class MainActivity() : AppCompatActivity(), IWeatherClientListener {

    var lbTemp: TextView? = null
    var lbTempLike: TextView? = null
    var lbCity: TextView? = null
    var lbPressure: TextView? = null
    var lbWindly: TextView? = null
    var lbCloud: TextView? = null
    var lbWeather: TextView? = null
    var lbHint: TextView? = null

    var swipeRefresher: SwipeRefreshLayout? = null
    var client: WeatherClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lbTemp = findViewById(R.id.lb_temp)
        lbTempLike = findViewById(R.id.lb_temp_like)
        lbCity = findViewById(R.id.lb_city)
        lbPressure = findViewById(R.id.lb_pressure)
        lbWindly = findViewById(R.id.lb_windly)
        lbCloud = findViewById(R.id.lb_cloud)
        lbWeather = findViewById(R.id.lb_weather)
        lbHint = findViewById(R.id.lb_hint)

        swipeRefresher = findViewById(R.id.swipe_refresher)
        swipeRefresher?.setColorSchemeColors(getColor(R.color.colorAccent))
        swipeRefresher?.setOnRefreshListener {
            Log.e("SWIPE", "call to refresh")
            client?.update()
            Handler().postDelayed({ cancleSwipeUpdate() }, 5000)
        }
        client = WeatherClient(this, this)

        findViewById<ImageButton>(R.id.btn_setting)?.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        client?.update()
    }

    private fun cancleSwipeUpdate() {
        runOnUiThread {
            swipeRefresher?.isRefreshing = false
        }
    }

    override fun onError() {
        Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show()
    }

    override fun onCity(city: String) {
        lbCity?.text = city
    }

    override fun onWeatherReceive(data: WeatherData) {
        lbTemp?.text = "${data.temp} \u2103"
        lbTempLike?.text =
            "Ощущается как ${data.temp_like} \u2103"
        lbPressure?.text = "${data.pressure} мм"
        lbWeather?.text = "${data.humidity} %"
        lbWindly?.text = "${data.wind} м/с"
        lbCloud?.text = "${data.cloud} %"
        lbWeather?.text = data.weather_desc
        cancleSwipeUpdate()
    }

    override fun onWeatherHint(hint: String) {
        lbHint?.text = hint
    }

    override fun onCityList(citys: List<CityInfo>) {

    }
}
