package com.sogoamobile.weatherapp.data.cities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sogoamobile.weatherapp.repository.CitiesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CitiesViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<CitiesTable>>
    private val repository: CitiesRepository

    init {
        val citiesDao = CitiesDatabase.getDatabase(application).citiesDao()
        repository = CitiesRepository(citiesDao)
        readAllData = repository.readAllData
    }

    fun addCities(citiesTable: CitiesTable){
        viewModelScope.launch ( Dispatchers.IO) {
            repository.addCities(citiesTable)
        }
    }

    fun updateCities(citiesTable: CitiesTable){
        viewModelScope.launch ( Dispatchers.IO) {
            repository.updateCities(citiesTable)
        }
    }
}