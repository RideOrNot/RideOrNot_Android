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

    private val _nearestStation = MutableLiveData<String>()
    val nearestStation: LiveData<String> = _nearestStation

    private val database = StationDatabase.getInstance(context)
    private var stationDao: StationDao = database!!.stationDao()
    private var lineDao: LineDao = database!!.lineDao()

    // 해당 역의 모든 도착 정보 얻기
    fun loadArrivalInfo(stationId: String) {
        viewModelScope.launch {
            val arrivalInfoList = arrivalRepository.getArrivalListByStationId(stationId).arrivalList
            updateLineIdsInArrivalList(arrivalInfoList)
        }
    }

    // 해당 도착 정보의 line_Id 리스트를 호선 이름(예: 3호선)으로 변경하여 도착 정보 업데이트
    private fun updateLineIdsInArrivalList(arrivalList: List<Arrival>) {
        viewModelScope.launch {
            val updatedArrivalList = withContext(Dispatchers.IO) {
                arrivalList.map { arrival ->
                    val newLineId = lineDao.getLineNameById(arrival.lineId.toInt())

                    arrival.copy(lineId = newLineId)
                }
            }
            _arrivalInfoList.value = updatedArrivalList
        }
    }


    // 현재 위치에서 가장 가까운 지하철 역의 이름 얻기
    @SuppressLint("MissingPermission")
    fun showNearestStationName(fusedLocationClient: FusedLocationProviderClient) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLocation = Location("Station").apply {
                        latitude = location.latitude
                        longitude = location.longitude
                    }

                    // 가장 가까운 지하철 역 찾기
                    viewModelScope.launch {
                        val subwayStations = withContext(Dispatchers.IO) { stationDao.getAll() }

                        val nearestStation = findNearestStation(subwayStations, currentLocation)
                        val nearestStationName = nearestStation.stationName
                        _nearestStation.value = nearestStationName

                        Log.e("[Home] nearestStationName", nearestStationName)

                        loadArrivalInfo(nearestStationName)
                    }
                } else {
                    // 현재 위치 정보를 얻지 못한 경우
                    Log.e("[Home] nearestStationName", "현재 위치 정보를 얻지 못했습니다.")
                }
            }
    }


    private fun findNearestStation(stations: List<Station>, currentLocation: Location): Station {
        var nearestStation: Station? = null
        var minDistance = Float.MAX_VALUE

        for (station in stations) {
            val stationLocation = Location("Station")
            stationLocation.latitude = station.stationLatitude
            stationLocation.longitude = station.stationLongitude

            val distance = currentLocation.distanceTo(stationLocation)
            if (distance < minDistance) {
                nearestStation = station
                minDistance = distance
            }
        }

        return nearestStation!!
    }

}