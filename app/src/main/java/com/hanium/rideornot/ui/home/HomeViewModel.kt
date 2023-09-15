package com.hanium.rideornot.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.hanium.rideornot.App.Companion.lastStationHistoryRepository
import com.hanium.rideornot.App.Companion.lineRepository
import com.hanium.rideornot.App.Companion.stationRepository
import com.hanium.rideornot.data.response.Arrival
import com.hanium.rideornot.domain.*
import com.hanium.rideornot.repository.ArrivalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(context: Context, private val arrivalRepository: ArrivalRepository) :
    ViewModel() {

    private val _arrivalInfoList = MutableLiveData<List<Arrival>>()
    val arrivalInfoList: LiveData<List<Arrival>> = _arrivalInfoList

    private val _lineList = MutableLiveData<List<Line>>()
    val lineList: LiveData<List<Line>> = _lineList

    private val _nearestStation = MutableLiveData<String>()
    val nearestStation: LiveData<String> = _nearestStation

    val currentTime = MutableLiveData<String>()

    var switchRideChecked = MutableLiveData<Boolean>()  // 승차 알림 스위치 ON/OFF 여부

    // 최근 역
    private val _lastStation = MutableLiveData<List<LastStationHistory>>()
    val lastStation: LiveData<List<LastStationHistory>> = _lastStation


    // 해당 역의 모든 도착 정보 얻기
    fun loadArrivalInfo(stationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val arrivalInfoList = arrivalRepository.getArrivalListByStationId(stationId).arrivalList
            val updatedArrivalList = updateLineIdsInArrivalList(arrivalInfoList)

            val currentTime = arrivalRepository.getArrivalListByStationId(stationId).currentTime

            withContext(Dispatchers.Main) {
                _arrivalInfoList.value = updatedArrivalList
                updateCurrentTime(currentTime)
            }
        }
    }

    // 해당 도착 정보의 line_Id 리스트를 호선 이름(예: 3호선)으로 변경하여 도착 정보 반환
    private suspend fun updateLineIdsInArrivalList(arrivalList: List<Arrival>): List<Arrival> {
        return arrivalList.map { arrival ->
            val newLineId = lineRepository.getLineNameById(arrival.lineId.toInt())
            arrival.copy(lineId = newLineId)
        }
    }

    // 최근 역 조회
    fun loadLastStationHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastStationHistory = lastStationHistoryRepository.getAllLastStations()
            _lastStation.postValue(lastStationHistory)
        }
    }

    // 최근 역 삭제
    fun deleteLastStationHistory(lastStationHistory: LastStationHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            lastStationHistoryRepository.deleteLastStation(lastStationHistory)
        }
    }

    // 현재 위치에서 가장 가까운 지하철 역의 이름 얻기
    @SuppressLint("MissingPermission")
    fun showNearestStationName(fusedLocationClient: FusedLocationProviderClient) {
        val locationRequest =
            LocationRequest.Builder(1000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val currentLocation = Location("Station").apply {
                        latitude = location.latitude
                        longitude = location.longitude
                    }

                    // 가장 가까운 지하철 역 찾기
                    viewModelScope.launch {
                        val nearestStation = findNearestStation(currentLocation)
                        _nearestStation.value = nearestStation.stationName

                        loadArrivalInfo(nearestStation.stationName)
                    }
                } else {
                    // 현재 위치 정보를 얻지 못한 경우
                    Log.e("[Home] nearestStationName", "현재 위치 정보를 얻지 못했습니다.")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    suspend fun findNearestStation(currentLocation: Location): Station {
        val subwayStations = stationRepository.getAll()

        var nearestStation: Station? = null
        var minDistance = Float.MAX_VALUE

        for (station in subwayStations) {
            val stationLocation = Location("Station").apply {
                latitude = station.stationLatitude
                longitude = station.stationLongitude
            }

            val distance = currentLocation.distanceTo(stationLocation)
            if (distance < minDistance) {
                nearestStation = station
                minDistance = distance
            }
        }

        return nearestStation!!
    }

    // 도착 정보 호출 시간 업데이트
    private fun updateCurrentTime(time: String) {
        currentTime.value = formatRefreshTime(time)
    }

    fun updateSwitchCheck(isChecked: Boolean) {
        switchRideChecked.value = isChecked
    }

    /**
     * 새로고침 시간을 받아서 12시간제로 형식화된 문자열로 반환
     * @param refreshTime 새로고침 시간 (예: "2023-07-17 오후 16:14:14")
     * @return 12시간제 형식으로 형식화된 시간 (예: "오후 04:14")
     */
    private fun formatRefreshTime(refreshTime: String): String {
        // 문자열에서 시간 부분과 오전/오후 부분을 추출
        val timeParts = refreshTime.split(" ")[2].split(":")
        val hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()
        val amPmIndicator = refreshTime.split(" ")[1]  // 오전 또는 오후

        // 12시간제로 변환
        val convertedHours = if (hours % 12 == 0) 12 else hours % 12

        return "$amPmIndicator ${String.format("%02d", convertedHours)}:${
            String.format(
                "%02d",
                minutes
            )
        }"
    }

}