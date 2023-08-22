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

class NotificationService : Service() {

    private lateinit var arrivalRepository: ArrivalRepository
    private val arrivalService = NetworkModule.getArrivalService()

    override fun onCreate() {
        super.onCreate()
        arrivalRepository = ArrivalRepository(ArrivalRemoteDataSourceImpl(arrivalService))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nearestStationExitId = intent?.getIntExtra("nearestStationExit", -1)
        if (nearestStationExitId != -1) {
            if (nearestStationExitId != null) {
                applicationScope.launch {
                    val content = loadRideNotification(nearestStationExitId)  // 양재 - 207 /nearestStationExitId
                    NotificationManager.createNotification(
                        applicationContext,
                        NotificationModel(
                            1,
                            ContentType.RIDE,
                            1,
                            "승차 알림",
                            content
                        )
                    )
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun loadRideNotification(exitId: Int): String {
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

        // 원래는 도착 정보가 없으면 푸시알림을 보내면 안되지만
        // 테스트 중이므로 지하철 역 근처에 오면 도착 정보가 없어도 푸시알림 보냄
        return if (rideNotification.rideNotificationList.isEmpty()) {
            "도착 정보가 없습니다."
        } else {
            // 예시: "서울역(1호선)에서 광운대행 - 시청방면 열차를 타려면 37초 동안 빠르게 걸으세요"
            val lineId = lineRepository.getLineNameById(rideNotification.rideNotificationList[0].lineId.toInt())
            val rideNotificationContent = rideNotification.rideNotificationList[0]
            var rideMessage =
                "지금 ${rideNotificationContent.stationName}역(${lineId}) ${rideNotificationContent.destination} 열차를 타려면 ${rideNotificationContent.message}"

            // 다음 열차 도착 정보도 있으면, 같이 안내
            if (rideNotification.rideNotificationList.size > 1) {
                val nextLineId = lineRepository.getLineNameById(rideNotification.rideNotificationList[1].lineId.toInt())
                val nextRideNotificationContent = rideNotification.rideNotificationList[1]
                val nextRideMessage = "${nextRideNotificationContent.stationName}역(${nextLineId}) ${nextRideNotificationContent.destination} 열차를 타려면 ${nextRideNotificationContent.message}"
                rideMessage += "\n" +  nextRideMessage
            }

            rideMessage
        }
    }
}