package com.sogoamobile.weatherapp.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sogoamobile.weatherapp.adapter.CitiesForecastAdapter
import com.sogoamobile.weatherapp.common.Common
import com.sogoamobile.weatherapp.databinding.FragmentHomeBinding
import com.sogoamobile.weatherapp.retrofit.IOpenWeatherMap
import com.sogoamobile.weatherapp.retrofit.RetrofitClient
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.lang.String
import kotlin.Boolean
import kotlin.TODO
import kotlin.plus
import kotlin.toString


class HomeFragment : Fragment() , SearchView.OnQueryTextListener{


    private var imgWeatherIcon: ImageView? = null
    private var mTxtTemp: TextView? = null
    private var mTxtWeatherDesc: TextView? = null
    private var mTxtCity: TextView? = null
    private var mTxtDateTime: TextView? = null
    private var loading: ProgressBar? = null
    private var cdvFav: CardView? = null
    var searchView: SearchView? = null
    private lateinit var adapter: CitiesForecastAdapter

    private var dbLat = "0.0"
    private var dbLng = "0.0"

    private var compositeDisposable: CompositeDisposable? = null
    private var mService: IOpenWeatherMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        val retrofit: Retrofit? = RetrofitClient.instance
        mService = retrofit?.create(IOpenWeatherMap::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // search
        searchView = binding.layoutHomeAppbar.searchView
        searchView?.setOnQueryTextListener(this)

        // recycler view
         adapter =
            CitiesForecastAdapter(requireContext(), compositeDisposable!!, mService!!, Common().getCitiesList())
        val recyclerView = binding.recyclerCitiesForecast
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // check location permissions
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

                        fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(requireContext())
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                // Got last known location. In some rare situations this can be null.
                                dbLat = location?.latitude.toString()
                                dbLng = location?.longitude.toString()
                            }
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

        imgWeatherIcon = binding.imgWeatherIcon
        mTxtTemp = binding.txtTemp
        mTxtWeatherDesc = binding.txtWeatherDesc
        mTxtCity = binding.txtCurrentLocation
        mTxtDateTime = binding.txtDateTime

        loading = binding.loadingF
        cdvFav = binding.cdvFav

        cdvFav?.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToWeatherFragment(
                lat = dbLat,
                long = dbLng
            )
            findNavController().navigate(action)
        }


        val weatherInfoTab = binding.layoutHomeAppbar.imgWeatherInfoTab
        weatherInfoTab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToWeatherFragment(
                lat = dbLat,
                long = dbLng
            )
            findNavController().navigate(action)
        }

        //search tab

        val searchTab = binding.layoutHomeAppbar.imgSearchTab
        val searchView = binding.layoutHomeAppbar.searchView
        val cdvSearchView = binding.layoutHomeAppbar.cdvSearchView
        searchTab.setOnClickListener {
            searchTab.visibility = View.GONE
            cdvSearchView.visibility = View.VISIBLE
        }

        // current weather
        getCurrentWeatherInformation()

        return view
    }

    private fun getCurrentWeatherInformation() {

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
                    ).into(imgWeatherIcon)
                    mTxtCity?.text = weatherResult?.name
                    mTxtWeatherDesc?.text = weatherResult?.weather?.get(0)?.description
                    mTxtTemp?.text =
                        StringBuilder(String.valueOf(weatherResult?.main?.temp?.toInt()))
                            .append("°C").toString()

                    //date
                    mTxtDateTime!!.text =
                        Common().convertUnixToDate(weatherResult?.dt!!.toLong()) + "\n\n" + Common().convertUnixToHour(weatherResult?.dt!!.toLong())

                    // change background
                    binding.imgBg1.setBackgroundResource(
                        Common().changeBackgroundImage(
                            weatherResult?.weather?.get(0)?.description!!
                        )
                    )

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

    override fun onQueryTextSubmit(query: kotlin.String?): Boolean {
        adapter?.filter?.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: kotlin.String?): Boolean {
        adapter?.filter?.filter(newText)
        return false
    }

}