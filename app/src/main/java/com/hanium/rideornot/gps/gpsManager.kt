package com.hanium.rideornot.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.hanium.rideornot.GeofenceBroadcastReceiver
import com.hanium.rideornot.MainActivity


private const val LOCATION_REQUEST_PERMISSION_REQUEST_CODE: Int = 1
private const val PRIORITY_LOCATION: @Priority Int = Priority.PRIORITY_HIGH_ACCURACY
private const val MAX_LOCATION_UPDATE_THREAD_COUNT = 1 // startLocationUpdate 를 동시에 호출할 수 있는 최대 스레드 수

object GpsManager {

    var lastLocation: Location? = null
    val geofenceList = MutableList<Geofence?>(100) { null }

    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var cancellationTokenSource: CancellationTokenSource
    private var locationUpdateInterval: Long = 2000;
    private var locationUpdateThreadCount = 0


    // private val REQUEST_PERMISSION_LOCATION = 10

    private lateinit var geofencingClient: GeofencingClient

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (locationUpdateThreadCount >= MAX_LOCATION_UPDATE_THREAD_COUNT) {
            return
        }
        locationUpdateThreadCount++

        locationRequest = LocationRequest.create().apply {
            interval = locationUpdateInterval
            priority = PRIORITY_LOCATION
        }

        var tempIndex = 0;
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation

                tempIndex++
                Log.d(
                    "locationUpdate-attempt $tempIndex",
                    "latitude: ${lastLocation?.latitude.toString()}, longitude: ${lastLocation?.longitude.toString()}"
                )
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
        if (locationUpdateThreadCount <= 0) {
            return
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        locationUpdateThreadCount--
    }

    fun initGpsManager(activity: MainActivity) {
        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                // TODO: request codes 관리 방법 모색
                LOCATION_REQUEST_PERMISSION_REQUEST_CODE
            )
        }
        // 초기화
        mainActivity = activity
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        geofencingClient = LocationServices.getGeofencingClient(mainActivity)
        val cancellationToken: CancellationToken = cancellationTokenSource.token
    }

    /** 지오펜스를 생성하고 모니터링할 지오펜스 목록에 추가 */
    fun addGeofence(
        id: String,
        latitude: Double,
        longitude: Double,
        radius: Float,
        duration: Long,
        //@Geofence.GeofenceTransition transitionType: Int
    ) {
        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // 전달받은 인수로 지오펜스 객체 생성
            val geoEnter = Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(duration)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
            val geoExit = Geofence.Builder()
                .setRequestId(id+1)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(duration)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
            val geoList: List<Geofence> = listOf(geoEnter, geoExit)
            geofenceList.add(geoEnter)

            // 모니터링할 지오펜스 목록에 생성한 객체를 추가
            geofencingClient.addGeofences(
                // INITIAL_TRIGGER 의 동작에 대한 정보
                // https://stackoverflow.com/questions/46712729/what-is-the-purpose-of-setinitialtrigger-on-geofencingrequest
                buildGeofencingRequest(geoList, GeofencingRequest.INITIAL_TRIGGER_ENTER),
                geofencePendingIntent
            )
                .run {
                    addOnSuccessListener {
                        Log.d("GeofenceAdditionSuccess", "Geofence added successfully")
                    }
                    addOnFailureListener {
                        Log.d("GeofenceAdditionFailure", exception?.message.toString())
                    }
                }
        } else {
            Log.d("Location Permission check", "Permission Denied")
        }
    }



    fun removeGeofences(requestIdList: List<String>) {
        geofencingClient.removeGeofences(requestIdList)
    }

    private fun buildGeofencingRequest(
        list: List<Geofence?>,
        @GeofencingRequest.InitialTrigger geofenceTrigger: Int
    ): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(geofenceTrigger)
            addGeofences(list)
        }.build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(mainActivity, GeofenceBroadcastReceiver::class.java)
        // "We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences()."
        // 라고 공식 문서에 쓰여 있었으나, 안드로이드 버전업 후 FLAG_IMMUTABLE 혹은 FLAG_MUTABLE 를 요구
        // FLAG_IMMUTABLE을 선택할 경우 intent가 GeofenceEvent 정보를 담을 수 없게 되어 문제가 생겼었음
        // https://stackoverflow.com/questions/74174663/geofencingevent-fromintentintent-returns-null
        PendingIntent.getBroadcast(mainActivity, 0, intent, PendingIntent.FLAG_MUTABLE)
    }
}