package com.sogoamobile.weatherapp

import com.sogoamobile.weatherapp.common.Common
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

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
    fun changeFavouriteImageIsCorrect() {
        assertEquals(Common().changeFavouriteImage(true), R.drawable.ic_heart_white)
        assertEquals(Common().changeFavouriteImage(false), R.drawable.ic_heart_outline)
    }
}