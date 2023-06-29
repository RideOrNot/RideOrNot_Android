package com.hanium.rideornot

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.widget.Button
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.gps.GpsManager
import android.view.*
import com.hanium.rideornot.data.ArrivalResponse
import com.hanium.rideornot.data.ArrivalService
import com.hanium.rideornot.notification.ContentType
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.notification.NotificationModel


class HomeFragment : Fragment(), ArrivalView {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.btnPushMessaging.setOnClickListener {
            getArrivalInfo()
            // 승차 알림 테스트
//            val notificationManager = NotificationManager
//            notificationManager.createNotification(
//                requireContext(), NotificationModel(
//                    1, ContentType.RIDE, 1, "승차 알림", "지금 10초 뛰면, 태릉입구역(7호선)에서 석남행 열차를 탈 수 있어요"
//              F  )
//            )
        }

        return binding.root
    }

    var tempGeofenceIndex = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addGeofenceButton: Button? = view.findViewById(R.id.addGeofenceButton)
        addGeofenceButton?.setOnClickListener {
            var tempGeofenceId = "test-$tempGeofenceIndex"
            GpsManager.addGeofence(
                tempGeofenceId.toString(),
                GpsManager.lastLocation!!.latitude,
                GpsManager.lastLocation!!.longitude,
                1000f,
                100000
            )
            tempGeofenceIndex++
        }

        val startLocationUpdateButton: Button? = view.findViewById(R.id.startLocationUpdateButton)
        startLocationUpdateButton?.setOnClickListener {
            GpsManager.startLocationUpdates()
        }

        val stopLocationUpdateButton: Button? = view.findViewById(R.id.stopLocationUpdateButton)
        stopLocationUpdateButton?.setOnClickListener {
            GpsManager.stopLocationUpdates()

        }

    }

    // 열차 도착 정보 조회
    private fun getArrivalInfo() {
        val arrivalService = ArrivalService()
        arrivalService.setArrivalView(this)

        arrivalService.getArrivalInfo("서울") // 임의로 서울역
    }

    override fun onArrivalSuccess(result: ArrivalResponse) {
        // 승차 알림 테스트
        // "지금 10초 뛰면, 서울역(1호선)에서 광운대행 - 시청방면 열차를 탈 수 있어요"
        val notificationContent =
            "지금 " + result.arrivalTime + "초 뛰면, " + "서울역" + "(" +
                    result.lineName + "호선)에서 " + result.destination + " 열차를 탈 수 있어요"

        val notificationManager = NotificationManager
        notificationManager.createNotification(
            requireContext(), NotificationModel(
                1,
                ContentType.RIDE,
                1,
                "승차 알림",
                notificationContent
            )
        )
    }

    override fun onArrivalFailure(code: Int, message: String) {
    }

}

interface ArrivalView {
    fun onArrivalSuccess(result: ArrivalResponse)
    fun onArrivalFailure(code: Int, message: String)
}