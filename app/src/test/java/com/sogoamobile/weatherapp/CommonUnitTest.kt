package com.sogoamobile.weatherapp

import com.sogoamobile.weatherapp.common.Common
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommonUnitTest {

    private val longDate:Long = 1212580300

    @Test
    fun convertUnixToDateIsCorrect() {
        assertEquals(Common().convertUnixToDate(longDate), "Wed, 4 Jun, 2008")
    }

    @Test
    fun convertUnixToHourIsCorrect() {
        assertEquals(Common().convertUnixToHour(longDate), "2:51 pm")
    }

    @Test
    fun changeBackgroundImageIsCorrect() {
        assertEquals(Common().changeBackgroundImage("rainy"), R.drawable.landscape_day_rain_mobile)
        assertEquals(Common().changeBackgroundImage("clear"), R.drawable.landscape_day_mobile)
    }

    @Test
    fun changeFavouriteImageIsCorrect() {
        assertEquals(Common().changeFavouriteImage(true), R.drawable.ic_heart_white)
        assertEquals(Common().changeFavouriteImage(false), R.drawable.ic_heart_outline)
    }
}