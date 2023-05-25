package com.hanium.rideornot

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource

private const val LOCATION_REQUEST_PERMISSION_REQUEST_CODE: Int = 1
private const val PRIORITY_LOCATION: @Priority Int = Priority.PRIORITY_HIGH_ACCURACY
private const val MAX_THREAD_COUNT = 1

// 싱글톤 객체

object LocationService {

    var lastLocation: Location? = null

    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var cancellationTokenSource: CancellationTokenSource
    private var locationUpdateInterval: Long = 2000;
    private var threadCount = 0;

    // private val REQUEST_PERMISSION_LOCATION = 10

    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = MutableList<Geofence?>(100) { null }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (threadCount > 0) {
            return
        }
        threadCount++

        locationRequest = LocationRequest.create().apply{
            interval = locationUpdateInterval
            priority = PRIORITY_LOCATION
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
        if (threadCount <= 0) {
            return
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        threadCount--
    }

    fun initLocationService(activity: MainActivity) {
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        val cancellationToken: CancellationToken = cancellationTokenSource.token
        // FINE_LOCATION, COARSE_LOCATION 의 permission check
        // TODO: 두 권한 체크를 분리해야 할지?
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
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