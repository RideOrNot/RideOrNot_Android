package com.hanium.rideornot

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hanium.rideornot.databinding.FragmentHomeBinding
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

}