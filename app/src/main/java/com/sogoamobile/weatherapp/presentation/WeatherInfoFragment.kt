package com.sogoamobile.weatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.R.id.txt_sunset
import com.sogoamobile.weatherapp.adapter.WeatherForecastAdapter
import com.sogoamobile.weatherapp.common.Common
import com.sogoamobile.weatherapp.databinding.FragmentWeatherInfoBinding
import com.sogoamobile.weatherapp.model.WeatherForecastResult
import com.sogoamobile.weatherapp.retrofit.IOpenWeatherMap
import com.sogoamobile.weatherapp.retrofit.RetrofitClient
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.lang.String
import kotlin.Long
import kotlin.toString


class WeatherInfoFragment : Fragment() {

    companion object {
        const val long = "long"
        const val lat = "lat"
    }

    private val instance: WeatherInfoFragment? = null
    private var txt_city_name: TextView? = null
    private var txt_humidity: TextView? = null
    private var txt_visibility: TextView? = null
    private var txt_sunset: TextView? = null
    private var txt_pressure: TextView? = null
    private var txt_description: TextView? = null
    private var txt_date_time: TextView? = null
    private var txt_wind: TextView? = null
    private var txt_temperature: TextView? = null
    private var txt_geo_cord: TextView? = null
    private var img_weather: ImageView? = null
    private var weather_panel: LinearLayout? = null
    private var loading: ProgressBar? = null

    private var compositeDisposable: CompositeDisposable? = null
    private var mService: IOpenWeatherMap? = null

    private var recycler_forecast: RecyclerView? = null

    private var dbLat = "0.0"
    private var dbLng = "0.0"

    private var _binding: FragmentWeatherInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            dbLat = it.getString(lat).toString()
            dbLng = it.getString(long).toString()
        }

        compositeDisposable = CompositeDisposable()
        val retrofit: Retrofit? = RetrofitClient.instance
        mService = retrofit?.create(IOpenWeatherMap::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherInfoBinding.inflate(inflater, container, false)
        val view = binding.root



        img_weather = binding.imgWeather
        txt_city_name = binding.txtCityName
        txt_date_time = binding.txtDateTime
        txt_description = binding.txtDescription
        txt_humidity = binding.txtHumidity
        txt_pressure = binding.txtPressure
        txt_visibility = binding.txtVisibility
        txt_sunset = binding.txtSunset
        txt_wind = binding.txtWind
        txt_temperature = binding.txtTemperature
        weather_panel = binding.weatherPanel
        loading = binding.loading


        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        //current weather
                        getWeatherInformation()
                        // weather forecast
                        getForecastWeatherInformation()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        "Permission Denied",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }).check()

        return view
    }


    private fun getWeatherInformation() {

        compositeDisposable?.add(
            mService!!.getWeatherByLatLng(
                dbLat, dbLng,
                Common().apiKey,
                "metric"
            )
            !!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ weatherResult -> //Load Image
                    Picasso.get().load(
                        StringBuilder(Common().imageUrl)
                            .append(weatherResult?.weather?.get(0)?.icon)
                            .append(".png").toString()
                    ).into(img_weather)

                    //city
                    txt_city_name?.text = weatherResult?.name

                    //temperature
                    txt_temperature?.text = java.lang.StringBuilder(
                        String.valueOf(weatherResult?.main?.temp?.toInt())
                    )
                        .append("°C").toString()

                    //date
                    txt_date_time!!.text =
                        "Updated " + Common().convertUnixToDate(weatherResult?.dt!!.toLong())

                    //pressure
                    txt_pressure?.text = java.lang.StringBuilder(
                        String.valueOf(
                            weatherResult.main?.pressure
                        )
                    )
                        .append(" hPa").toString()

                    //humidity
                    txt_humidity?.text = java.lang.StringBuilder(
                        String.valueOf(
                            weatherResult.main?.humidity
                        )
                    ).append(" %").toString()

                    //wind
                    txt_wind?.text = java.lang.StringBuilder(
                        String.valueOf(
                            (weatherResult.wind?.speed?.times(3.6))?.toInt()
                        )
                    ).append(" km/h").toString()

                    //visibility
                    txt_visibility?.text = java.lang.StringBuilder(
                        String.valueOf(
                            weatherResult.visibility / 1000
                        )
                    ).append(" km/h").toString()

                    txt_sunset?.text = Common().convertUnixToHour(
                        weatherResult.sys?.sunset!!.toLong()
                    )
                    txt_geo_cord?.text = java.lang.StringBuilder("[")
                        .append(weatherResult.coord.toString())
                        .append("]").toString()
                    weather_panel!!.visibility = View.VISIBLE
                    loading!!.visibility = View.GONE
                }, { throwable ->
                    loading?.visibility = View.GONE
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        throwable.message!!, Snackbar.LENGTH_LONG
                    ).show()
                })
        )
    }

    private fun getForecastWeatherInformation() {



        compositeDisposable!!.add(
            mService!!.getForecastWeatherByLatLng(
                dbLat, dbLng,
                Common().apiKey,
                "metric"
            )
            !!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ weatherForecastResult ->
                    displayForecastWeather(
                        weatherForecastResult!!,
                    )
                }
                ) { throwable ->
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        throwable.message!!, Snackbar.LENGTH_LONG
                    ).show()
                }
        )

    }

    private fun displayForecastWeather(
        weatherForecastResult: WeatherForecastResult

    ) {
//        Log.d("WeatherInfoFragment", weatherForecastResult.list?.get(0)?.weather?.get(0)?.description.toString())
        val adapter = WeatherForecastAdapter(requireContext(), weatherForecastResult)
        val recyclerView = binding.recyclerForecast
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter
    }

}