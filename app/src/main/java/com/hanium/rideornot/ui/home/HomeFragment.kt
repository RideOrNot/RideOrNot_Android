package com.hanium.rideornot.ui.home

import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hanium.rideornot.App.Companion.getApplicationContext
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.ui.common.ViewModelFactory
import com.hanium.rideornot.ui.dialog.BaseDialog

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels { ViewModelFactory(requireContext()) }

    private lateinit var homeNearbyNotificationRVAdapter: HomeNearbyNotificationRVAdapter
    private var homeLastStationRVAdapter = HomeLastStationRVAdapter(ArrayList())

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var stationName: String = ""

    private var isFirstAnimation = true  // 앱 최초 실행 여부

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // TDL
        // 최근 역 RecyclerView 연결 필요

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(getApplicationContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        homeNearbyNotificationRVAdapter =
            HomeNearbyNotificationRVAdapter(requireContext(), ArrayList())
        binding.rvNearbyNotification.adapter = homeNearbyNotificationRVAdapter
        binding.rvLastStation.adapter = homeLastStationRVAdapter


        // 플레이스토어 정책 위반(반응하지 않는 버튼) 때문에 임시로 토스트 출력
        // TODO: 최근 역 편집 기능 구현
        binding.btnRecentStationEdit.setOnClickListener{
            BaseDialog(requireContext() as AppCompatActivity).show(getString(R.string.toast_not_yet_implemented))
        }
        binding.rvLastStation.setOnClickListener {
            BaseDialog(requireContext() as AppCompatActivity).show(getString(R.string.toast_not_yet_implemented))
        }

        binding.clNoticeBtn.setOnClickListener {
            BaseDialog(requireContext() as AppCompatActivity).show(getString(R.string.toast_not_yet_implemented))
        }


        // 주변 알림 - 근처 역, 도착 정보 조회
        viewModel.showNearestStationName(fusedLocationClient)
        viewModel.nearestStation.observe(viewLifecycleOwner) {
            stationName = it
        }

        // 최근 역 - 더미 데이터
//        val stations = Station(100, 37.948605, 127.061003, "소요산", 1001)
//        val stationList = ArrayList<Station>()
//        stationList.add(stations)
//        homeLastStationRVAdapter.updateData(stationList)

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

            // 홈 화면에 표시되는 역 갱신
            viewModel.showNearestStationName(fusedLocationClient)
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
//        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate360)
//        binding.fabRefresh.startAnimation(rotateAnimation)
//
//        // 주변 알림 - 도착 정보 재조회
//        viewModel.showNearestStationName(fusedLocationClient)
//        viewModel.nearestStation.observe(viewLifecycleOwner) {
//            stationName = it
//        }
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


}