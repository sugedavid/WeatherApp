package com.sogoamobile.weatherapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.common.Common
import com.sogoamobile.weatherapp.model.WeatherForecastResult
import com.squareup.picasso.Picasso
import java.util.*


class WeatherForecastAdapterB(
    var context: Context,
    private var weatherForecastResult: WeatherForecastResult
) :
    RecyclerView.Adapter<WeatherForecastAdapterB.MyViewHolder>() {

    private var mTxtDesc: TextView? = null
    private var mTxtTemp: TextView? = null
    private var mTxtTime: TextView? = null
    private var imgCloud: ImageView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.item_weather_forecastb, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val desc: String? = weatherForecastResult.list?.get(position)?.weather?.get(0)?.description


        //Load Image
        Picasso.get().load(
            StringBuilder(Common().imageUrl)
                .append(weatherForecastResult.list?.get(position)?.weather?.get(0)?.icon)
                .append(".png").toString()
        ).into(holder.img_weather)
        val time: String? = weatherForecastResult.list?.get(position)?.dt?.let {
            Common().convertUnixToDate(
                it
            )
        }
        holder.txt_date_time.text = time
        holder.txt_description.text = desc
        holder.txt_temperature.setText(
            StringBuilder(
                java.lang.String.valueOf(
                    weatherForecastResult.list?.get(
                        position
                    )?.main?.temp
                )
            ).append("°C")
        )


    }

    override fun getItemCount(): Int {
        return weatherForecastResult.list!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_date_time: TextView
        var txt_description: TextView
        var txt_temperature: TextView
        var img_weather: ImageView
        var lnForecast: LinearLayout

        init {
            img_weather = itemView.findViewById(R.id.img_weather)
            txt_date_time = itemView.findViewById(R.id.txt_date)
            txt_description = itemView.findViewById(R.id.txt_description)
            txt_temperature = itemView.findViewById(R.id.txt_temperature)
            lnForecast = itemView.findViewById(R.id.lnForecast)
        }
    }

}
