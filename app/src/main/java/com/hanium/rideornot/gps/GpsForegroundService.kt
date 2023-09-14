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
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.R
import com.hanium.rideornot.notification.NotificationService
import com.hanium.rideornot.ui.home.HomeFragment

class GpsForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "ride_or_not_foreground_service_channel"
        const val FOREGROUND_NOTIFICATION_ID = 123
        const val ACTION_STOP_FOREGROUND_SERVICE = "stop"
        var isServiceRunning = false  // 서비스 진행 여부
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
            .setContentTitle("위치 탐색")
            .setContentText("주변 지하철 역을 찾고 있습니다")
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
            "위치 탐색",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkLocationProviderStatus()

        isServiceRunning = true

        // 실시간 위치 업데이트 시작
        GpsManager.startLocationUpdates()

        if (intent == null) {
            return START_NOT_STICKY
        }
        if (intent.action == ACTION_STOP_FOREGROUND_SERVICE) {
            // 승차 알림 스위치 OFF 설정
            val switchIntent = Intent("ACTION_UPDATE_SWITCH_STATE")
            switchIntent.putExtra("SWITCH_STATE", false)
            sendBroadcast(switchIntent)

            // 포그라운드 서비스 종료
            NotificationManagerCompat.from(this).cancel(FOREGROUND_NOTIFICATION_ID)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelfResult(startId)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        isServiceRunning = false
        NotificationManagerCompat.from(this).cancel(FOREGROUND_NOTIFICATION_ID)
        // BroadcastReceiver 등록 해제
        unregisterReceiver(locationProviderReceiver)

        GpsManager.stopLocationUpdates()
        val allGeofenceIds = GpsManager.geofenceList.mapNotNull { it?.requestId }
        GpsManager.removeGeofences(allGeofenceIds)
    }


    // 위치 제공자 상태 확인
    private fun checkLocationProviderStatus() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled || !isNetworkEnabled) {
            // 위치 제공자가 비활성화된 경우
            // 포그라운드 서비스 종료
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

}