package com.sogoamobile.weatherapp.common

import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.data.cities.CitiesTable
import java.text.SimpleDateFormat
import java.util.*

class Common {
    val apiKey = R.string.apiKey
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

    fun changeBackgroundImage(condition: String, c: Calendar): Int {
        val am = c.get(Calendar.AM_PM) === Calendar.AM

        return if (condition.contains("rain") && am) {
            R.drawable.landscape_day_rain_mobile
        } else if (!condition.contains("rain") && am) {
            R.drawable.landscape_day_mobile
        } else if (condition.contains("rain") && !am) {
            R.drawable.landscape_night_rain_mobile
        } else if (!condition.contains("rain") && !am) {
            R.drawable.landscape_night_mobile
        } else {
            R.drawable.landscape_day_mobile
        }

    }

    fun changeFavouriteImage(isFavourite: Boolean): Int {


        return if (isFavourite) {
            R.drawable.ic_heart_white
        } else {
            R.drawable.ic_heart_outline
        }
    }

    fun getCitiesList(): ArrayList<CitiesTable> {
        return arrayListOf(
            CitiesTable(0,"Beijing", false),
            CitiesTable(1,"Delhi", false),
            CitiesTable(2,"Dhaka", false),
            CitiesTable(3,"Guangzhou", false),
            CitiesTable(4,"Istanbul", false),
            CitiesTable(5,"Berlin", false),
            CitiesTable(6,"Calcutta", false),
            CitiesTable(7,"Seoul", false),
            CitiesTable(8,"Sao Paulo", false),
            CitiesTable(9,"Singapore", false),
            CitiesTable(10,"Copenhagen", false),
            CitiesTable(11,"Seoul", false),
            CitiesTable(12,"Chandigarh", false),
            CitiesTable(13,"Amsterdam", false),
            CitiesTable(14,"Washington DC", false),
            CitiesTable(15,"Zurich", false),
            CitiesTable(16,"Montreal", false),
            CitiesTable(17,"Berlin", false),
            CitiesTable(18,"New York", false),
            CitiesTable(19,"Tokyo", false),

        )
    }
}