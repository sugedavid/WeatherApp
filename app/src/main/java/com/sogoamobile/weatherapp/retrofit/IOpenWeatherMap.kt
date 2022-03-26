package com.sogoamobile.weatherapp.retrofit

import com.sogoamobile.weatherapp.model.WeatherForecastResult
import com.sogoamobile.weatherapp.model.WeatherResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface IOpenWeatherMap {
    @GET("weather")
    fun getWeatherByLatLng(
        @Query("lat") lat: String?,
        @Query("lon") lng: String?,
        @Query("appid") appid: String?,
        @Query("units") unit: String?
    ): Observable<WeatherResult?>?

    @GET("weather")
    fun getWeatherByCity(
        @Query("q") q: String?,
        @Query("appid") appid: String?,
        @Query("units") unit: String?
    ): Observable<WeatherResult?>?

    @GET("forecast")
    fun getForecastWeatherByLatLng(
        @Query("lat") lat: String?,
        @Query("lon") lng: String?,
        @Query("appid") appid: String?,
        @Query("units") unit: String?
    ): Observable<WeatherForecastResult?>?

    @GET("forecast")
    fun getForecastWeatherCity(
        @Query("q") q: String?,
        @Query("appid") appid: String?,
        @Query("units") unit: String?
    ): Observable<WeatherForecastResult?>?

}