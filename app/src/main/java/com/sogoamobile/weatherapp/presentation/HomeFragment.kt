package com.sogoamobile.weatherapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.sogoamobile.weatherapp.R
import com.sogoamobile.weatherapp.common.Common
import com.sogoamobile.weatherapp.retrofit.IOpenWeatherMap
import com.sogoamobile.weatherapp.retrofit.RetrofitClient
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.lang.String


class HomeFragment : Fragment() {


    private var compositeDisposable: CompositeDisposable? = null
    private var mService: IOpenWeatherMap? = null
    private var instance: HomeFragment? = null

    private var imgWeatherIcon: ImageView? = null
    private var mTxtTemp: TextView? = null
    private var mTxtWeatherDesc: TextView? = null
    private var mTxtFarmLoc: TextView? = null

    private var loading: ProgressBar? = null
    private var cdvFav: CardView? = null


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

        imgWeatherIcon = itemView.findViewById(R.id.imgWeatherIcon)
        mTxtTemp = itemView.findViewById(R.id.txtTemp)
        mTxtWeatherDesc = itemView.findViewById(R.id.txtWeatherDesc)
        mTxtFarmLoc = itemView.findViewById(R.id.txtCurrentLocation)

        loading = itemView.findViewById(R.id.loadingF)
        cdvFav = itemView.findViewById(R.id.cdvFav)

        cdvFav?.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putDouble("lat", 39.8865)
            bundle.putDouble("lng", -83.4483)
            val weatherInfoFragment = WeatherInfoFragment()
            weatherInfoFragment.arguments = bundle
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            transaction.replace(R.id.mainFragment, weatherInfoFragment)
            transaction.commit()
        })


        getWeatherInformation()

        return itemView
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
                    ).into(imgWeatherIcon)
                    mTxtFarmLoc?.text = weatherResult?.name
                    mTxtWeatherDesc?.text = weatherResult?.weather?.get(0)?.description
                    /*txt_description.setText(new StringBuilder("Weather in ")
                                    .append(weatherResult.getName()).toString());*/
                    mTxtTemp?.text = StringBuilder(String.valueOf(weatherResult?.main?.temp))
                        .append("°C").toString()
                    //txt_date_time.setText("Updated "+Common.convertUnixToDate(weatherResult.getDt()));
                    loading?.visibility = View.GONE
                }, { throwable ->
                    loading?.visibility = View.GONE
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        throwable.message!!, Snackbar.LENGTH_LONG
                    ).show()
                })
        )
    }


}