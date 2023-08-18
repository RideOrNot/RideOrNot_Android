package com.hanium.rideornot.gps

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hanium.rideornot.App
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.R
import com.hanium.rideornot.domain.StationDatabase
import com.hanium.rideornot.repository.StationRepository

class GpsForegroundService : Service() {

    private lateinit var stationRepository: StationRepository

    companion object {
        private const val CHANNEL_ID = "ride_or_not_foreground_service_channel"
        const val FOREGROUND_NOTIFICATION_ID = 123
        const val ACTION_STOP_FOREGROUND_SERVICE = "stop"
    }

    private val locationProviderReceiver = object : BroadcastReceiver() {
        private var isGpsEnabled: Boolean = false
        private var isNetworkEnabled: Boolean = false

        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let { action ->
                if (action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val locationManager =
                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    isNetworkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    // 위치 제공자가 비활성화되면
                    if (!isGpsEnabled || !isNetworkEnabled) {
                        // 포그라운드 서비스 종료
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val stationDao = StationDatabase.getInstance(App.getApplicationContext())!!.stationDao()
        stationRepository = StationRepository(stationDao)

        GpsManager.setGpsForegroundService(this)

        // 위치 서비스 상태 변경 브로드캐스트 리시버 등록
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationProviderReceiver, filter)

        // 포그라운드 서비스 알림 채널 생성
        createNotificationChannel()

        // 포그라운드 서비스 알림을 눌렀을 때 실행될 Intent 생성
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 서비스 종료 버튼을 눌렀을 때 실행될 Intent 생성
        val stopIntent = Intent(this, GpsForegroundService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("탈래말래 Foreground Service")
            .setContentText("주변 지하철 역을 탐색 중입니다")
            .setSmallIcon(R.drawable.ic_app_logo_round)
            .setColor(ContextCompat.getColor(this, R.color.blue))
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_app_logo_round, "서비스 종료", stopPendingIntent)
            .setOngoing(true)
            .setVibrate(null)
            .setSound(null)
            .build()

        // 포그라운드 서비스 시작
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "포그라운드 서비스",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 실시간 위치 업데이트 시작
        GpsManager.startLocationUpdates()

        if (intent == null) {
            return START_NOT_STICKY
        }
        if (intent.action == ACTION_STOP_FOREGROUND_SERVICE) {
            // 포그라운드 서비스 종료
            NotificationManagerCompat.from(this).cancel(FOREGROUND_NOTIFICATION_ID)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelfResult(startId)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        NotificationManagerCompat.from(this).cancel(FOREGROUND_NOTIFICATION_ID)
        // BroadcastReceiver 등록 해제
        unregisterReceiver(locationProviderReceiver)

        GpsManager.stopLocationUpdates()
        val allGeofenceIds = GpsManager.geofenceList.mapNotNull { it?.requestId }
        GpsManager.removeGeofences(allGeofenceIds)
    }


    private fun sendPushNotification() {
        // 승차 알림 테스트
        // 지금 위치로부터 지하철역까지 걸어가는 시간은 추가 안 한 상태.
        // "지금 10초 뛰면, 서울역(1호선)에서 광운대행 - 시청방면 열차를 탈 수 있어요"
//        val notificationContent =
//            "지금 " + result[0].arrivalTime + "초 뛰면, " + "서울역" + "(" +
//                    result[0].lineName + "호선)에서 " + result[0].destination + " 열차를 탈 수 있어요"
//
//        val notificationManager = NotificationManager
//        notificationManager.createNotification(
//            requireContext(), NotificationModel(
//                1,
//                ContentType.RIDE,
//                1,
//                "승차 알림",
//                notificationContent
//            )
//        )
    }


}