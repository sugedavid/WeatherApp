package com.sogoamobile.weatherapp.common

import android.location.Location
import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.model.CityFavourite
import java.text.SimpleDateFormat
import java.util.*

class Common {
    val apiKey = "120ac724beb69c2bf18a9a3b4707aa19"
    val baseUrl = "https://api.openweathermap.org/data/2.5/"
    val imageUrl = "https://openweathermap.org/img/w/"

    fun convertUnixToDate(dt: Long): String? {
        val date = Date(dt * 1000L)
        val simpleDateFormat = SimpleDateFormat("EEE, d MMM, yyy")
        return simpleDateFormat.format(date)
    }

    fun convertUnixToHour(dt: Long): String? {
        val date = Date(dt * 1000L)
        val simpleDateFormat = SimpleDateFormat("h:mm a")
        return simpleDateFormat.format(date)
    }

    fun changeBackgroundImage(condition: String): Int {
        return if (condition.contains("rain")) {
            R.drawable.landscape_day_rain_mobile
        } else {
            R.drawable.landscape_day_mobile
        }
    }

    fun getCitiesList(): ArrayList<CityFavourite> {
        return arrayListOf(
            CityFavourite("Berlin", false),
            CityFavourite("Calcutta", false),
            CityFavourite("Seoul", false),
            CityFavourite("Sao Paulo", false),
            CityFavourite("Sydney", false)
        )
    }
}