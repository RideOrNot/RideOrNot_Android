package com.hanium.rideornot.ui.home

import android.content.*
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hanium.rideornot.App.Companion.getApplicationContext
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.domain.LastStationHistory
import com.hanium.rideornot.gps.GpsForegroundService
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.ui.common.ViewModelFactory
import com.hanium.rideornot.ui.dialog.OptionDialog


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels { ViewModelFactory(requireContext()) }

    private lateinit var homeNearbyNotificationRVAdapter: HomeNearbyNotificationRVAdapter
    private lateinit var homeLastStationRVAdapter: HomeLastStationRVAdapter

    private lateinit var sharedPreferences: SharedPreferences  // 승차 알림 스위치 상태 저장

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var stationName: String = ""

    private var isFirstAnimation = true  // 앱 최초 실행 여부

    companion object {
        var switchRideChecked = false

        const val PREFS_NAME = "rideSwitch"
        const val SWITCH_IS_CHECKED_KEY = "switchRide"
    }

    private val switchUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_UPDATE_SWITCH_STATE") {
                val isSwitchChecked = intent.getBooleanExtra("SWITCH_STATE", false)
                // 스위치 상태를 업데이트하고 UI에 반영
                switchRideChecked = isSwitchChecked
                viewModel.updateSwitchCheck(isSwitchChecked)
                updateSwitchSetting(isSwitchChecked)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(getApplicationContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // 승차 알림 스위치 설정 초기화
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        initializeSwitch()

        // 승차 알림 스위치 상태 변경 이벤트 리스너 설정
        binding.switchRideNotification.setOnCheckedChangeListener { button, isChecked ->
            switchRideChecked = isChecked
            viewModel.updateSwitchCheck(isChecked)
            button.setOnClickListener {
                updateSwitchSetting(isChecked)
            }

            // 변경된 스위치 상태를 SharedPreferences에 저장
            sharedPreferences.edit().putBoolean(SWITCH_IS_CHECKED_KEY, isChecked).apply()
        }


        // RV Adapter 연결
        homeNearbyNotificationRVAdapter =
            HomeNearbyNotificationRVAdapter(requireContext(), ArrayList())
        binding.rvNearbyNotification.adapter = homeNearbyNotificationRVAdapter

        // 최근 역
        viewModel.loadLastStationHistory()
        viewModel.lastStation.observe(viewLifecycleOwner) {
            homeLastStationRVAdapter = HomeLastStationRVAdapter(it as ArrayList<LastStationHistory>)
            binding.rvLastStation.adapter = homeLastStationRVAdapter
        }

        // 주변 알림 - 근처 역, 도착 정보 조회
        viewModel.showNearestStationName(fusedLocationClient)
        viewModel.nearestStation.observe(viewLifecycleOwner) {
            stationName = it
        }


        viewModel.arrivalInfoList.observe(viewLifecycleOwner) { arrivalInfoList ->
            // 같은 lineId를 갖는 도착 정보끼리 리스트로 묶어 RecyclerView에 전달
            val groupedArrivalInfoMap = arrivalInfoList.groupBy { it.lineId }
            val groupedArrivalInfoList = groupedArrivalInfoMap.values.toList()
            homeNearbyNotificationRVAdapter.updateData(groupedArrivalInfoList)

            // 로딩 표시
            showShimmerItem()

            // 도착 정보가 없는 경우
            if (arrivalInfoList.isEmpty())
                binding.tvNotExistArrivalInfo.visibility = View.VISIBLE
            else
                binding.tvNotExistArrivalInfo.visibility = View.INVISIBLE
        }

        // 새로 고침
        binding.fabRefresh.setOnClickListener {
            val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate360)
            binding.fabRefresh.startAnimation(rotateAnimation)

            // 도착 정보 API 호출
            viewModel.loadArrivalInfo(stationName)
        }

        // 주변 알림 - 더보기
        binding.btnNearbyNotificationMoreInfo.setOnClickListener {
            val stationName = viewModel.nearestStation.value
            stationName?.let {
                // 역 상세정보 화면으로 이동
                findNavController().navigate(
                    HomeFragmentDirections.actionFragmentHomeToActivityStationDetail(stationName)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate360)
        binding.fabRefresh.startAnimation(rotateAnimation)

        // 주변 알림 - 도착 정보 재조회
        viewModel.showNearestStationName(fusedLocationClient)
        viewModel.nearestStation.observe(viewLifecycleOwner) {
            stationName = it
        }

        // 승차 알림 스위치
        val filter = IntentFilter("ACTION_UPDATE_SWITCH_STATE")
        requireActivity().registerReceiver(switchUpdateReceiver, filter)

        // 최근 역 갱신
        viewModel.loadLastStationHistory()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(switchUpdateReceiver)
    }

    /**
     * 로딩 (Shimmer) 표시
     */
    private fun showShimmerItem() {
        binding.sflHome.stopShimmer()
        binding.sflHome.visibility = View.GONE

        if (isFirstAnimation) {
            // 애니메이션 설정
            val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

            fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    binding.tvNearbyNotificationStationName.visibility = View.VISIBLE
                    binding.tvNearbyNotificationCurrentTime.visibility = View.VISIBLE
                    binding.rvNearbyNotification.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {
                    isFirstAnimation = false
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            })

            binding.tvNearbyNotificationStationName.startAnimation(fadeInAnimation)
            binding.tvNearbyNotificationCurrentTime.startAnimation(fadeInAnimation)
            binding.rvNearbyNotification.startAnimation(fadeInAnimation)
        } else {
            // 첫 애니메이션이 이미 진행된 경우, 뷰들의 가시성을 바로 변경
            binding.tvNearbyNotificationStationName.visibility = View.VISIBLE
            binding.tvNearbyNotificationCurrentTime.visibility = View.VISIBLE
            binding.rvNearbyNotification.visibility = View.VISIBLE
        }
    }

    /**
     * 승차 알림 스위치 설정 초기화
     */
    private fun initializeSwitch() {
        // 스위치의 이전 설정 값을 가져와서 적용
        val isSwitchChecked = sharedPreferences.getBoolean(SWITCH_IS_CHECKED_KEY, false)
        switchRideChecked = isSwitchChecked
        viewModel.updateSwitchCheck(isSwitchChecked)

        if (isSwitchChecked) {
            binding.tvRideNotification.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue
                )
            )

            GpsManager.initGpsManager(requireActivity() as MainActivity)

            if (GpsManager.arePermissionsGranted(requireContext()) && NotificationManager.isPermissionGranted(
                    requireContext()
                ) && !GpsForegroundService.isServiceRunning
            ) {
                // 포그라운드 서비스 시작
                val serviceIntent = Intent(requireContext(), GpsForegroundService::class.java)
                ContextCompat.startForegroundService(requireContext(), serviceIntent)
            }
        } else {
            binding.tvRideNotification.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_700
                )
            )
        }
    }


    /**
     * 승차 알림 스위치 상태에 따라 스위치 설정 업데이트
     * @param isSwitchChecked 스위치의 상태
     */
    private fun updateSwitchSetting(isSwitchChecked: Boolean) {
        if (isSwitchChecked) {
            binding.tvRideNotification.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue
                )
            )

            // 승차 알림 안내 Dialog 표시
            val optionDialog = OptionDialog(activity as AppCompatActivity)
            optionDialog.btnClickListener {
                if (it) {
                    // 확인 클릭 시
                    GpsManager.initGpsManager(requireContext() as MainActivity)

                    if (GpsManager.arePermissionsGranted(requireContext()) && NotificationManager.isPermissionGranted(
                            requireContext()
                        ) && !GpsForegroundService.isServiceRunning && viewModel.switchRideChecked.value == true
                    ) {
                        // 포그라운드 서비스 시작
                        val serviceIntent =
                            Intent(requireContext(), GpsForegroundService::class.java)
                        ContextCompat.startForegroundService(requireContext(), serviceIntent)
                    }
                } else {
                    // 취소 클릭 시
                    switchRideChecked = false
                    viewModel.updateSwitchCheck(false)

                    binding.tvRideNotification.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_700
                        )
                    )
                }
            }
            optionDialog.show(getString(R.string.ride_notification_info))
        } else {
            binding.tvRideNotification.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_700
                )
            )
            // 포그라운드 서비스 종료
            val serviceIntent = Intent(requireContext(), GpsForegroundService::class.java)
            requireActivity().stopService(serviceIntent)
        }
    }

}