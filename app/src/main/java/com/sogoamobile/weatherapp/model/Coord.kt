package com.sogoamobile.weatherapp.model

import java.lang.StringBuilder

class Coord(var lat: Double, var lon: Double) {
    override fun toString(): String {
        return StringBuilder("[").append(lat).append(',').append(lon).append(']').toString()
    }
}