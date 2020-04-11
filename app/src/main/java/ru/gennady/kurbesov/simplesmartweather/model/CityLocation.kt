package ru.gennady.kurbesov.simplesmartweather.model

class CityLocation {
    var lat: Double
    var lon: Double

    constructor(){
        lat = 0.0
        lon = 0.0
    }

    constructor(latitude: Double, longitude: Double) {
        lat = latitude
        lon = longitude
    }
}