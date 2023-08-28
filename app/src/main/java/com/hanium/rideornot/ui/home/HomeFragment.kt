package com.hanium.rideornot.ui.home

import android.os.Bundle

import androidx.fragment.app.Fragment
import com.hanium.rideornot.databinding.FragmentHomeBinding
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hanium.rideornot.R
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.ui.common.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels { ViewModelFactory(requireContext()) }

    private lateinit var homeNearbyNotificationRVAdapter: HomeNearbyNotificationRVAdapter
    private var homeLastStationRVAdapter = HomeLastStationRVAdapter(ArrayList())

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var station: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // TDL
        // 최근 역 RecyclerView 연결 필요

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

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

        // 주변 알림 - 근처 역, 도착 정보 조회
        viewModel.showNearestStationName(fusedLocationClient)
        viewModel.nearestStation.observe(viewLifecycleOwner) {
            station = it
        }

        // 최근 역 - 더미 데이터
        val stations = Station(100, 37.948605, 127.061003, "소요산", 1001)
        val stationList = ArrayList<Station>()
        stationList.add(stations)
        homeLastStationRVAdapter.updateData(stationList)


        viewModel.arrivalInfoList.observe(viewLifecycleOwner) { arrivalInfoList ->
            // 같은 lineId를 갖는 도착 정보끼리 리스트로 묶어 RecyclerView에 전달
            val groupedArrivalInfoMap = arrivalInfoList.groupBy { it.lineId }
            val groupedArrivalInfoList = groupedArrivalInfoMap.values.toList()
            homeNearbyNotificationRVAdapter.updateData(groupedArrivalInfoList)

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
            viewModel.loadArrivalInfo(station)
        }

        // 주변 알림 - 더보기
        binding.btnNearbyNotificationMoreInfo.setOnClickListener {
            val stationName = viewModel.nearestStation.value
            stationName?.let {
                viewModel.loadLineList(stationName)

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
            station = it
        }
    }

}