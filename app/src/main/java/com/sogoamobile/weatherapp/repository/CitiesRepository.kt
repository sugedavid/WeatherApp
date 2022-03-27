package com.sogoamobile.weatherapp.repository

import androidx.lifecycle.LiveData
import com.sogoamobile.weatherapp.data.cities.CitiesDao
import com.sogoamobile.weatherapp.data.cities.CitiesTable

class CitiesRepository(private val citiesDao: CitiesDao) {

    val readAllData: LiveData<List<CitiesTable>> = citiesDao.readAllData()

    fun addCities(citiesTable: CitiesTable){
        citiesDao.addCities(citiesTable)
    }

    fun updateCities(citiesTable: CitiesTable){
        citiesDao.updateCity(citiesTable)
    }
}