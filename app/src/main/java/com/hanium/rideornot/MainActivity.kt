package com.hanium.rideornot

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import com.hanium.rideornot.databinding.ActivityMainBinding

private const val LOCATION_REQUEST_PERMISSION_REQUEST_CODE: Int = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    public var lastLocation: Location? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var cancellationTokenSource: CancellationTokenSource


    // private val REQUEST_PERMISSION_LOCATION = 10

    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = MutableList<Geofence?>(100) { null }

   private var locationUpdateInterval: Long = 2000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()

        initLocationService()
        startLocationUpdates()

        //geofencingClient = LocationServices.getGeofencingClient(this)
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frm_main, HomeFragment())
            .commitAllowingStateLoss()
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.favoriteFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, FavoriteFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.settingFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, SettingFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

//    private fun getStationLocation(longitude:Float, latitude:Float): Task<Location>? {
//       return null
//    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationRequest = LocationRequest.create().apply{
            interval = locationUpdateInterval
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        var tempIndex = 0;
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation

                tempIndex++
                Log.d("locationUpdate-attempt $tempIndex",
                    "longitude: ${lastLocation?.longitude.toString()}, latitude: ${lastLocation?.latitude.toString()}")
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            // 위치 업데이트를 처리할 스레드의 루퍼를 전달
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun initLocationService() {
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val cancellationToken: CancellationToken = cancellationTokenSource.token
        // FINE_LOCATION, COARSE_LOCATION 의 permission check
        // TODO: 두 권한 체크를 분리해야 할지?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                // TODO: request codes 관리 방법 모색
                LOCATION_REQUEST_PERMISSION_REQUEST_CODE
            )
        }
    }



}
