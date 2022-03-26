package com.sogoamobile.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.common.Common
import com.sogoamobile.weatherapp.data.Notes
import com.sogoamobile.weatherapp.model.WeatherForecastResult
import com.sogoamobile.weatherapp.presentation.fragments.HomeFragmentDirections
import com.sogoamobile.weatherapp.retrofit.IOpenWeatherMap
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_notes.view.*
import kotlinx.android.synthetic.main.item_cities_forecast.view.*
import kotlinx.android.synthetic.main.item_notes.view.*


class CitiesForecastAdapter(
    var context: Context,
    private var compositeDisposable: CompositeDisposable,
    private var mService: IOpenWeatherMap,
    private var cities: List<String>
) :
    RecyclerView.Adapter<CitiesForecastAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.item_cities_forecast, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val city = cities[position]

        compositeDisposable.add(
            mService.getForecastWeatherCity(
                city,
                Common().apiKey,
                "metric"
            )
            !!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ weatherForecastResult ->

                    //Load weather icon
                    Picasso.get().load(
                        StringBuilder(Common().imageUrl)
                            .append(weatherForecastResult?.list?.get(position)?.weather?.get(0)?.icon)
                            .append(".png").toString()
                    ).into(holder.itemView.imgWeatherIcon)

                    //city name
                    holder.itemView.txtCityName.text = city

                    //temperature
                    holder.itemView.txtTemperature.text = StringBuilder(
                        java.lang.String.valueOf(
                            weatherForecastResult?.list?.get(
                                position
                            )?.main?.temp?.toInt()
                        )
                    ).append("°C")

                    val time = weatherForecastResult?.list?.get(position)?.dt?.let {
                        (Common().convertUnixToHour(
                            it
                        ) )
                    }

                    val date = weatherForecastResult?.list?.get(position)?.dt?.let {
                        (Common().convertUnixToDate(
                            it
                        ) )
                    }

                    // date and time
                    holder.itemView.txtDateTime.text = "$date \n\n$time"


                    holder.itemView.setOnClickListener {
//
                        val action = HomeFragmentDirections.actionHomeFragmentToWeatherFragment(
                            city =  city,
                        )
                        holder.itemView.findNavController().navigate(action)
                    }

                }
                ) { throwable ->
                    Toast.makeText(
                        context, throwable.message!!, Toast.LENGTH_LONG
                    ).show()
                }
        )


    }

    override fun getItemCount(): Int {
        return cities.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}
