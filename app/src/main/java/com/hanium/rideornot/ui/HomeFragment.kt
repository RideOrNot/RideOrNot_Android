package com.hanium.rideornot.ui

import android.app.Activity.*
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.hanium.rideornot.BuildConfig
import com.hanium.rideornot.data.ArrivalResponse
import com.hanium.rideornot.data.ArrivalService
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.notification.ContentType
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.notification.NotificationModel
import com.hanium.rideornot.signIn.SignInActivity


private const val REQ_ONE_TAP = 2

class HomeFragment : Fragment(), ArrivalView {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // getArrivalInfo()

        // 주변 알림 RecyclerView 연결 + 더미 데이터
        val arrivalData = ArrayList<ArrivalResponse>()
        arrivalData.add(ArrivalResponse(3, "남부터미널 방면", "3", "남부터미널"))
        val homeNearbyNotificationRVAdapter = HomeNearbyNotificationRVAdapter(arrivalData)
        binding.rvNearbyNotification.adapter = homeNearbyNotificationRVAdapter

        // 최근 역 RecyclerView 연결


        return binding.root
    }

    var tempGeofenceIndex = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (true) {
//            val signInIntent = Intent(requireActivity(), SignInActivity::class.java)
//            startActivity(signInIntent)
//        }
    }

    // 열차 도착 정보 조회
    private fun getArrivalInfo() {
        val arrivalService = ArrivalService()
        arrivalService.setArrivalView(this)

        // 열차 도착 정보 조회 API 호출
        arrivalService.getArrivalInfo("서울") // 임의로 서울역
    }

    override fun onArrivalSuccess(result: ArrivalResponse) {
        // 승차 알림 테스트
        // 지금 위치로부터 지하철역까지 걸어가는 시간은 추가 안 한 상태.
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
        Toast.makeText(context, "열차 도착 정보 조회 API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

}

interface ArrivalView {
    fun onArrivalSuccess(result: ArrivalResponse)
    fun onArrivalFailure(code: Int, message: String)
}