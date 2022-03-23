package com.sogoamobile.weatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.adapter.WeatherForecastAdapterB
import com.sogoamobile.weatherapp.common.Common
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

    private val instance: WeatherInfoFragment? = null
    private var txt_city_name: TextView? = null
    private var txt_humidity: TextView? = null
    private var txt_sunrise: TextView? = null
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

    private var dbLat = 0.0
    private var dbLng = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        compositeDisposable = CompositeDisposable()
        val retrofit: Retrofit? = RetrofitClient.instance
        mService = retrofit?.create(IOpenWeatherMap::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val itemView = inflater.inflate(R.layout.fragment_home, container, false)

        recycler_forecast?.setHasFixedSize(true)
        recycler_forecast?.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recycler_forecast = itemView.findViewById(R.id.recycler_forecast)


        img_weather = itemView.findViewById(R.id.img_weather)
        txt_city_name = itemView.findViewById(R.id.txt_city_name)
        txt_date_time = itemView.findViewById(R.id.txt_date_time)
        txt_description = itemView.findViewById(R.id.txt_description)
        txt_humidity = itemView.findViewById(R.id.txt_humidity)
        txt_pressure = itemView.findViewById(R.id.txt_pressure)
        txt_sunrise = itemView.findViewById(R.id.txt_sunrise)
        txt_sunset = itemView.findViewById(R.id.txt_sunset)
        txt_wind = itemView.findViewById(R.id.txt_wind)
        txt_temperature = itemView.findViewById(R.id.txt_temperature)
        weather_panel = itemView.findViewById(R.id.weather_panel)
        loading = itemView.findViewById(R.id.loading)


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
                        getWeatherInformation()
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

        return itemView
    }

    private fun displayForecastWeather(
        weatherForecastResult: WeatherForecastResult

    ) {
        val adapter = WeatherForecastAdapterB(requireContext(), weatherForecastResult)
        recycler_forecast?.adapter = adapter
    }

    private fun getWeatherInformation() {

        compositeDisposable?.add(
            mService!!.getWeatherByLatLng(
                "39.8865", "-83.4483",
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
                    txt_city_name?.text = weatherResult?.name
                    txt_temperature?.text = java.lang.StringBuilder(
                        String.valueOf(weatherResult?.main?.temp)
                    )
                        .append("°C").toString()
                    txt_date_time!!.text =
                        "Updated " + Common().convertUnixToDate(weatherResult?.dt as Long)
                    txt_pressure?.text = java.lang.StringBuilder(
                        String.valueOf(
                            weatherResult.main?.pressure
                        )
                    )
                        .append(" hpa").toString()
                    txt_humidity?.text = java.lang.StringBuilder(
                        String.valueOf(
                            weatherResult.main?.humidity
                        )
                    )
                        .append(" %").toString()
                    txt_sunrise?.text = Common().convertUnixToHour(
                        weatherResult.sys?.sunrise as Long
                    )
                    txt_sunset?.text = Common().convertUnixToHour(
                        weatherResult.sys?.sunset as Long
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
        if (arguments != null) {
            dbLat = requireArguments().getDouble("lat")
            dbLng = requireArguments().getDouble("lng")

            compositeDisposable!!.add(
                mService!!.getForecastWeatherByLatLng(
                    dbLat.toString(), dbLng.toString(),
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
    }


}