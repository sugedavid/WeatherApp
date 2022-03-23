package com.sogoamobile.weatherapp.common

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

class Common {
    val apiKey = "120ac724beb69c2bf18a9a3b4707aa19"
    val baseUrl = "https://api.openweathermap.org/data/2.5/"
    val imageUrl = "https://openweathermap.org/img/w/"
    var current_location: Location? = null

    fun convertUnixToDate(dt: Long): String? {
        val date = Date(dt * 1000L)
        val simpleDateFormat = SimpleDateFormat("HH:mm dd EEE")
        return simpleDateFormat.format(date)
    }

    fun convertUnixToHour(dt: Long): String? {
        val date = Date(dt * 1000L)
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        return simpleDateFormat.format(date)
    }
}