package com.sogoamobile.weatherapp.retrofit

import retrofit2.Retrofit
import com.sogoamobile.weatherapp.retrofit.RetrofitClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RetrofitClient {
    var instance: Retrofit? = null
        get() {
            if (instance == null)
                instance = Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return instance
        }
        private set
}