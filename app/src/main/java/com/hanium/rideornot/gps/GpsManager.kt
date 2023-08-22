package com.hanium.rideornot.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
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
private const val GEOFENCE_LIST_SIZE = 200
private const val SUFFIX_GEOFENCE_ID_ENTER = "-enter"
private const val SUFFIX_GEOFENCE_ID_EXIT = "-exit"

object GpsManager {

    var lastLocation: Location? = null
    val geofenceList: MutableList<Geofence?> = mutableListOf()

    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var cancellationTokenSource: CancellationTokenSource
    private var locationUpdateInterval: Long = 2000;
    private var locationUpdateThreadCount = 0

    private lateinit var geofencingClient: GeofencingClient

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
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                // 백그라운드 위치정보 접근권한 요청 삭제함
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

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (locationUpdateThreadCount >= MAX_LOCATION_UPDATE_THREAD_COUNT) {
            return
        }
        locationUpdateThreadCount++

        locationRequest = LocationRequest.create().apply {
            // 위치 수집 간격은 interval, fastestInterval 둘 다에 영향을 받는 것으로 보임.
            // interval만 설정하면 interval 값에 관계없이 간격이 1초로 고정되고,
            // fastestInterval만 설정하면 수집을 아예 하지 못하는 상황을 확인하였음
            interval = locationUpdateInterval
            fastestInterval = locationUpdateInterval
            priority = PRIORITY_LOCATION
        }

        var tempIndex = 0;
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                tempIndex++
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

    /** 지오펜스를 생성하고 모니터링할 지오펜스 목록에 추가.
     * Enter, Exit를 각각 트리거하는 지오펜스 두 개를 생성함.
     * 실제 requestId는 "{$requestId}-enter", "{$requestId}-exit"로 설정됨. */
    @SuppressLint("MissingPermission")
    fun addGeofence(
        requestId: String,
        latitude: Double,
        longitude: Double,
        radius: Float,
        duration: Long,
    ) {
        // 전달받은 인수로 지오펜스 객체 생성
        val geoEnter = Geofence.Builder()
            .setRequestId("$requestId$SUFFIX_GEOFENCE_ID_ENTER")
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(duration)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
        val geoExit = Geofence.Builder()
            .setRequestId("$requestId$SUFFIX_GEOFENCE_ID_EXIT")
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(duration)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
        val geoList: List<Geofence> = listOf(geoEnter, geoExit)

        // 모니터링할 지오펜스 목록에 생성한 객체를 추가
        geofencingClient.addGeofences(
            // INITIAL_TRIGGER 의 존재 의의에 대한 설명
            // https://stackoverflow.com/questions/46712729/what-is-the-purpose-of-setinitialtrigger-on-geofencingrequest
            buildGeofencingRequest(geoList, GeofencingRequest.INITIAL_TRIGGER_ENTER),
            geofencePendingIntent
        )
            .run {
                addOnSuccessListener {
                    Log.d("GeofenceAdditionSuccess", "Geofence added successfully")
                    geofenceList.add(geoEnter)
                    geofenceList.add(geoExit)
                }
                addOnFailureListener {
                    Log.d("GeofenceAdditionFailure", exception?.message.toString())
                }
            }
    }

    /** 제거할 지오펜스의 requestId 리스트를 전달하면, 해당하는 지오펜스들이 제거됨.
     * ex) requestId로 "test" 를 전달하면 requestId = "test-enter" || "test-exit" 에 해당하는 지오펜스가 제거됨. */
    fun removeGeofences(idList: List<String>) {
        geofencingClient.removeGeofences(idList)
        for (i in idList.indices)
            geofenceList.removeIf {
                it?.requestId == idList[i] + SUFFIX_GEOFENCE_ID_ENTER || it?.requestId == idList[i] + SUFFIX_GEOFENCE_ID_EXIT
            }
    }

    fun logGeofenceList() {
        var geoStr = ""
        for (i in geofenceList) {
            geoStr += i?.requestId.toString()
            geoStr += " "
        }
        Log.d("GeofenceList", geoStr)
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