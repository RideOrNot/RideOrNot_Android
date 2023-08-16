package com.hanium.rideornot.ui.home

import android.content.Intent
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
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.gps.GpsForegroundService
import com.hanium.rideornot.ui.common.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeNearbyNotificationRVAdapter: HomeNearbyNotificationRVAdapter
    private var homeLastStationRVAdapter = HomeLastStationRVAdapter(ArrayList())

    private val viewModel: HomeViewModel by viewModels { ViewModelFactory(requireContext()) }

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
        homeNearbyNotificationRVAdapter =
            HomeNearbyNotificationRVAdapter(requireContext(), ArrayList())

        // 주변 알림 - 근처 역, 도착 정보 조회
        viewModel.showNearestStationName(fusedLocationClient)
        viewModel.nearestStation.observe(viewLifecycleOwner) { nearestStation ->
            station = nearestStation
        }
        binding.rvNearbyNotification.adapter = homeNearbyNotificationRVAdapter
        binding.rvLastStation.adapter = homeLastStationRVAdapter

        // 최근 역 - 더미 데이터
        val stations = Station(100, 37.948605, 127.061003, "소요산", 1001)
        val stationList = ArrayList<Station>()
        stationList.add(stations)
        homeLastStationRVAdapter.updateData(stationList)


        viewModel.arrivalInfoList.observe(viewLifecycleOwner) { arrivalInfoList ->
            (station + "역").also { binding.tvNearbyNotificationStationName.text = it }

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
            val stationName =
                binding.tvNearbyNotificationStationName.text.toString().dropLast(1)
            viewModel.loadLineList(stationName)

            // 역 상세정보 화면으로 이동
            findNavController().navigate(
                HomeFragmentDirections.actionFragmentHomeToActivityStationDetail(stationName)
            )
        }

//        val addGeofenceButton: Button? = view.findViewById(R.id.addGeofenceButton)
//        addGeofenceButton?.setOnClickListener {
//            var tempGeofenceId = "test-$tempGeofenceIndex"
//            GpsManager.addGeofence(
//                tempGeofenceId.toString(),
//                GpsManager.lastLocation!!.latitude,
//                GpsManager.lastLocation!!.longitude,
//                1000f,
//                100000
//            )
//            tempGeofenceIndex++
//        }
//
//        val startLocationUpdateButton: Button? = view.findViewById(R.id.startLocationUpdateButton)
//        startLocationUpdateButton?.setOnClickListener {
//            GpsManager.startLocationUpdates()
//        }
//
//        val stopLocationUpdateButton: Button? = view.findViewById(R.id.stopLocationUpdateButton)
//        stopLocationUpdateButton?.setOnClickListener {
//            GpsManager.stopLocationUpdates()
//
//        }

    }

    override fun onResume() {
        super.onResume()
        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate360)
        binding.fabRefresh.startAnimation(rotateAnimation)

        // 주변 알림 - 도착 정보 재조회
        viewModel.showNearestStationName(fusedLocationClient)
        viewModel.nearestStation.observe(viewLifecycleOwner) { nearestStation ->
            station = nearestStation
        }
    }

}