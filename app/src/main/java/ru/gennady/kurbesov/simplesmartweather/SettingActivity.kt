package ru.gennady.kurbesov.simplesmartweather

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import ru.gennady.kurbesov.simplesmartweather.model.CityInfo
import ru.gennady.kurbesov.simplesmartweather.weather.IWeatherClientListener
import ru.gennady.kurbesov.simplesmartweather.weather.WeatherClient
import ru.gennady.kurbesov.simplesmartweather.weather.WeatherData


class SettingActivity : AppCompatActivity(), IWeatherClientListener {

    var txtCityName: AppCompatAutoCompleteTextView? = null
    var client: WeatherClient? = null
    private val cityNames = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        client = WeatherClient(this, this)

        initView()
    }

    override fun onStart() {
        super.onStart()
        txtCityName?.setText(WeatherClient.getCityName(this))
    }

    override fun onPause() {
        super.onPause()
        WeatherClient.setCity(this, txtCityName?.text.toString() ?: "Москва")
    }

    private fun initView() {
        txtCityName = findViewById(R.id.txt_city_name)
        txtCityName?.threshold = 3
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.select_dialog_singlechoice,
            cityNames
        )
        txtCityName?.setAdapter(adapter)
        txtCityName?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                return
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                txtCityName.let {
                    client?.searchCity(it!!.text.toString())
                }
            }
        })
        findViewById<ImageButton>(R.id.btn_back)?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onError() {

    }

    override fun onCity(city: String) {

    }

    override fun onWeatherReceive(data: WeatherData) {

    }

    override fun onWeatherHint(hint: String) {

    }

    override fun onCityList(citys: List<CityInfo>) {
        citys.forEach {
            if(!cityNames.contains(it.description)){
                cityNames.add(it.description)
                Log.d("CITYS", "Add new city ${it.description}")
            }
        }
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.select_dialog_singlechoice,
            cityNames
        )
        txtCityName?.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }
}
