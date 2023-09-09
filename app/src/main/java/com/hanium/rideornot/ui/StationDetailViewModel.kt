package com.hanium.rideornot.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.App.Companion.lastStationHistoryRepository
import com.hanium.rideornot.App.Companion.lineRepository
import com.hanium.rideornot.App.Companion.stationRepository
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.domain.*
import com.hanium.rideornot.repository.ArrivalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationDetailViewModel(context: Context, private val arrivalRepository: ArrivalRepository) :
    ViewModel() {

    private val _arrivalList = MutableLiveData<ArrivalResponse>()
    val arrivalList: LiveData<ArrivalResponse> = _arrivalList

    private val _lineList = MutableLiveData<List<Line>>()
    val lineList: LiveData<List<Line>> = _lineList

    private val _stationItem = MutableLiveData<Station>()
    val stationItem: LiveData<Station> = _stationItem

    val prevStationName = MutableLiveData<String>()
    val nextStationName = MutableLiveData<String>()


    // 해당 역, 호선의 도착 정보 얻기
    fun loadArrivalList(stationId: String, lineId: Int) {
        viewModelScope.launch {
            val arrivalList = withContext(Dispatchers.IO) {
                arrivalRepository.getArrivalList(stationId, lineId)
            }
            _arrivalList.value = arrivalList
        }
    }

    // 해당 역의 호선 목록 얻기
    fun loadLineList(stationName: String) {
        viewModelScope.launch {
            val lineId = stationRepository.findLineByName(stationName)
            val lineList = lineRepository.getLinesByIds(lineId) as ArrayList<Line>
            _lineList.value = lineList
        }
    }

    // 해당 역, 호선의 양옆 역 얻기
    fun loadStation(stationName: String, lineId: Int) {
        viewModelScope.launch {
            val stationItem = stationRepository.findStationByNameAndLineId(stationName, lineId)
            _stationItem.value = stationItem
        }
    }

    // 이전/이후 역 뷰 업데이트
    fun updateNeighboringStationView(station: Station) {
        prevStationName.value = when {
            station.beforeStationId1 == 0 -> "종착"
            station.beforeStationId2 == 0 -> station.beforeStation1
            else -> "${station.beforeStation1}/${station.beforeStation2}"
        }

        nextStationName.value = when {
            station.nextStationId1 == 0 -> "종착"
            station.nextStationId2 == 0 -> station.nextStation1
            else -> "${station.nextStation1}/${station.nextStation2}"
        }
    }

    // 최근 역 추가
    fun insertLastStationHistory(lastStationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var existingHistory = lastStationHistoryRepository.getAllLastStations()

            // 중복된 항목 삭제
            val duplicateItem = existingHistory.find { it.stationName == lastStationName }
            duplicateItem?.let {
                lastStationHistoryRepository.deleteLastStation(it)
            }

            lastStationHistoryRepository.insertLastStation(LastStationHistory(stationName = lastStationName))

            existingHistory = lastStationHistoryRepository.getAllLastStations()
            while (existingHistory.size > 5) {
                // 항목이 5개가 넘으면, 가장 오래된 항목 삭제
                val oldestItem = existingHistory.minByOrNull { it.lastStationHistoryId }
                oldestItem?.let {
                    lastStationHistoryRepository.deleteLastStation(it)
                }
            }
        }
    }

}