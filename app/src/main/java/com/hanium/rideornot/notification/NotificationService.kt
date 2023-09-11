package com.hanium.rideornot.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.hanium.rideornot.App.Companion.applicationScope
import com.hanium.rideornot.App.Companion.lineRepository
import com.hanium.rideornot.App.Companion.stationExitRepository
import com.hanium.rideornot.App.Companion.stationRepository
import com.hanium.rideornot.repository.ArrivalRepository
import com.hanium.rideornot.source.ArrivalRemoteDataSourceImpl
import com.hanium.rideornot.utils.NetworkModule
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NotificationService : Service() {

    private lateinit var arrivalRepository: ArrivalRepository
    private val arrivalService = NetworkModule.getArrivalService()
    private var stationName = ""

    private var timer: Timer? = Timer()
    private val updateInterval = 1 * 1000   // 업데이트 주기 (1초)

    override fun onCreate() {
        super.onCreate()
        arrivalRepository = ArrivalRepository(ArrivalRemoteDataSourceImpl(arrivalService))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nearestStationExitId = intent?.getIntExtra("nearestStationExit", -1)
        if (nearestStationExitId != -1) {
            if (nearestStationExitId != null) {
                createNotification(nearestStationExitId)

                // 주기적으로 알림 업데이트
                timer?.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        updateNotificationContent(nearestStationExitId)
                    }
                }, updateInterval.toLong(), updateInterval.toLong())

            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Service가 종료될 때 정리 작업 수행
        timer?.cancel()
        timer = null
    }

    // 초기 알림 생성
    private fun createNotification(nearestStationExitId: Int) {
        applicationScope.launch {
            val content =
                loadRideNotification(nearestStationExitId) // 502-홍대 236-서울 207-양재 / nearestStationExitId

            if (!content.isNullOrEmpty()) {
                NotificationManager.createNotification(
                    applicationContext,
                    NotificationModel(
                        1,
                        ContentType.RIDE,
                        1,
                        stationName,
                        "승차 알림",
                        content
                    )
                )
            }
        }
    }

    private fun updateNotificationContent(nearestStationExitId: Int) {
        applicationScope.launch {
            val content =
                loadRideNotification(nearestStationExitId) // 502-홍대 236-서울 207-양재 / nearestStationExitId

            if (!content.isNullOrEmpty()) {
                NotificationManager.updateNotification(
                    applicationContext,
                    NotificationModel(
                        1,
                        ContentType.RIDE,
                        1,
                        stationName,
                        "승차 알림",
                        content
                    )
                )
            }
        }
    }


    private suspend fun loadRideNotification(exitId: Int): ArrayList<String>? {
        // 푸시 알림 API 호출
        val stationExit = stationExitRepository.findStationExitById(exitId)
        val station = stationRepository.findStationById(stationExit.stationId)
        Log.e(
            "[NotificationService] loadRideNotification",
            exitId.toString() + " / " + station.stationName
        )
        val rideNotification = arrivalRepository.getRideNotification(
            station.stationName,
            stationExit.exitName
        )

        val rideNotifications = ArrayList<String>()

        // 원래는 도착 정보가 없으면 푸시알림을 보내면 안되지만
        // 테스트 중이므로 지하철 역 근처에 오면 도착 정보가 없어도 푸시알림 보냄 -> 주석 처리함
        return if (rideNotification.rideNotificationList.isNotEmpty()) {
            stationName = "${rideNotification.rideNotificationList[0].stationName}역"
            for (rideNotificationContent in rideNotification.rideNotificationList) {
                // 예시: "서울역(1호선)에서 광운대행 - 시청방면 열차를 타려면 37초 동안 빠르게 걸으세요"
                val lineId = lineRepository.getLineNameById(rideNotificationContent.lineId.toInt())
                val rideMessage =
                    "$lineId) ${rideNotificationContent.destination} 열차를 타려면 ${rideNotificationContent.message}"
                rideNotifications.add(rideMessage)
            }

            rideNotifications
        } else {
            null
        }
    }
}