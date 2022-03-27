package com.sogoamobile.weatherapp.data.cities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities_table")
data class CitiesTable (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val cityName: String,
    var isFavourite: Boolean
    )