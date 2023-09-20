package com.hanium.rideornot.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.hanium.rideornot.App.Companion.applicationScope
import com.hanium.rideornot.App.Companion.getApplicationContext
import com.hanium.rideornot.App.Companion.stationExitRepository
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.domain.StationExit
import com.hanium.rideornot.notification.NotificationManager
import kotlinx.coroutines.launch

const val LOCATION_PERMISSION_REQUEST_CODE: Int = 1
private const val PRIORITY_LOCATION: @Priority Int = Priority.PRIORITY_HIGH_ACCURACY
private const val MAX_LOCATION_UPDATE_THREAD_COUNT = 1 // startLocationUpdate 를 동시에 호출할 수 있는 최대 스레드 수
private const val GEOFENCE_LIST_SIZE = 200
private const val SUFFIX_GEOFENCE_ID_ENTER = "-enter"
private const val SUFFIX_GEOFENCE_ID_EXIT = "-exit"
private const val MAX_GEOFENCE_COUNT = 20

object GpsManager {

    var lastLocation: Location? = null
    val geofenceList: MutableList<Geofence?> = mutableListOf()

    private var mainActivity: MainActivity = MainActivity()
    private var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var cancellationTokenSource: CancellationTokenSource
    private var locationUpdateInterval: Long = 2000
    private var locationUpdateThreadCount = 0

    private var geofencingClient: GeofencingClient

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    private var gpsForegroundService: GpsForegroundService? = null
    var nearestStationExit: StationExit? = null

    init {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(getApplicationContext())
        geofencingClient = LocationServices.getGeofencingClient(getApplicationContext())
    }

    fun initGpsManager(activity: MainActivity) {
        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(
                activity,
                locationPermissions[0]
            ) != PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                activity,
                locationPermissions[1]
            ) != PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                activity,
                locationPermissions[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("위치 정보 접근 권한 허용 설정 안내").setCancelable(false)
            builder.setMessage(
                "이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
                        + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
                        + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
                        + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
                        + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
                        + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다."
            )
            builder.setPositiveButton("확인") { _, _ ->
                ActivityCompat.requestPermissions(
                    activity,
                    locationPermissions,
                    // Geofence 떄문에 백그라운드 위치정보 접근권한 요청 다시 추가함
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            builder.show()
        } else {
            NotificationManager.initNotificationManager(activity)
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

        locationRequest = LocationRequest.Builder(PRIORITY_LOCATION, locationUpdateInterval).apply {
            // 위치 수집 간격은 interval, fastestInterval 둘 다에 영향을 받는 것으로 보임.
            // interval만 설정하면 interval 값에 관계없이 간격이 1초로 고정되고,
            // fastestInterval만 설정하면 수집을 아예 하지 못하는 상황을 확인하였음
            setMinUpdateIntervalMillis(locationUpdateInterval)
        }.build()

        var tempGeofenceIndex = 1
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                //Log.e("[GpsManager] lastLocation", lastLocation.toString())
                applicationScope.launch {
                    // 새로운 위치 정보가 업데이트될 때마다 가까운 역 찾기 및 Geofence 추가
                    val geofenceRadius = 1000f
                    nearestStationExit = findNearestStationExit(lastLocation)
                    if (nearestStationExit != null) {
//                        addGeofence(
//                            "myStation",
//                            36.348853916,  //36.348853916  // lastLocation!!.latitude
//                            127.33261265,  //127.33261265  // lastLocation!!.longitude
//                            geofenceRadius,
//                            1000000
//                        )
                        // 이미 같은 좌표에 대한 Geofence가 있는지 확인 후, 없으면 Geofence 생성
                        if (!isDuplicateGeofence(nearestStationExit!!.stationLatitude, nearestStationExit!!.stationLongitude)) {
                            addGeofence(
                                "myStation-$tempGeofenceIndex",
                                nearestStationExit!!.stationLatitude,
                                nearestStationExit!!.stationLongitude,
                                geofenceRadius,
                                1000000
                            )
                            tempGeofenceIndex++
                        }

                        //Log.e("[GpsManager] nearestStationExit", nearestStationExit.toString())
                        //logGeofenceList()
                    }
                }
            }
        }

        handleLocationPermissionChange()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun setGpsForegroundService(service: GpsForegroundService) {
        gpsForegroundService = service
    }

    private fun handleLocationPermissionChange() {
        // 위치 권한 변경되었다면 포그라운드 서비스 종료 및 알림 제거
        if (!arePermissionsGranted(getApplicationContext())) {
            getApplicationContext().stopService(
                Intent(
                    getApplicationContext(),
                    GpsForegroundService::class.java
                )
            )
        }
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
        // 지오펜스 개수가 최대 개수보다 크면 오래된 지오펜스를 삭제
        if (geofenceList.size >= MAX_GEOFENCE_COUNT) {
            // 최근에 생성된 지오펜스 5개를 제외한 나머지 지오펜스를 가져와서 제거
            val geofencesToRemove = geofenceList.dropLast(5)
            val idsToRemove = geofencesToRemove.mapNotNull { it?.requestId }
            removeGeofences(idsToRemove)
            geofenceList.removeAll(geofencesToRemove)
        }

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
        intent.putExtra("nearestStationExit", nearestStationExit?.exitId)
        // "We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences()."
        // 라고 공식 문서에 쓰여 있었으나, 안드로이드 버전업 후 FLAG_IMMUTABLE 혹은 FLAG_MUTABLE 를 요구
        // FLAG_IMMUTABLE을 선택할 경우 intent가 GeofenceEvent 정보를 담을 수 없게 되어 문제가 생겼었음
        // https://stackoverflow.com/questions/74174663/geofencingevent-fromintentintent-returns-null

        // API 버전에 따라 동작하는 flag가 달라짐 (intent에 putExtra를 해줬으므로 31 이상에서는 FLAG_MUTABLE, 그 아래는 FLAG_CANCEL_CURRENT 로 작동)
        PendingIntent.getBroadcast(mainActivity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    // 위치 권한 허용 여부를 확인
    fun arePermissionsGranted(context: Context): Boolean {
        for (permission in locationPermissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // 이미 같은 좌표에 대한 Geofence가 있는지 확인
    private fun isDuplicateGeofence(latitude: Double, longitude: Double): Boolean {
        for (existingGeofence in geofenceList) {
            if (existingGeofence?.latitude == latitude && existingGeofence.longitude == longitude) {
                return true
            }
        }
        return false
    }

    // 가장 가까운 지하철 역 출구 찾기
    private suspend fun findNearestStationExit(currentLocation: Location?): StationExit? {
        val subwayStationExits = stationExitRepository.getAll()

        var nearestStationExit: StationExit? = null
        var minDistance = Float.MAX_VALUE

        for (stationExit in subwayStationExits) {
            val stationLocation = Location("Station")
            stationLocation.latitude = stationExit.stationLatitude
            stationLocation.longitude = stationExit.stationLongitude

            val distance = currentLocation?.distanceTo(stationLocation)
            if (distance != null) {
                if (distance < minDistance) {
                    nearestStationExit = stationExit
                    minDistance = distance
                }
            }
        }

        return nearestStationExit
    }
}