package com.hanium.rideornot

import android.os.Bundle

import android.util.Log
import androidx.fragment.app.Fragment
import android.widget.Button
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.gps.GpsManager
import android.view.*
import com.hanium.rideornot.notification.ContentType
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.notification.NotificationModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.btnPushMessaging.setOnClickListener {
            // 승차 알림 테스트
            val notificationManager = NotificationManager
            notificationManager.createNotification(
                requireContext(), NotificationModel(
                    1, ContentType.RIDE, 1, "승차 알림", "지금 10초 뛰면, 태릉입구역(7호선)에서 석남행 열차를 탈 수 있어요"
                )
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationTestButton: Button? = view.findViewById(R.id.locationTestButton)
        locationTestButton?.setOnClickListener {

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

}