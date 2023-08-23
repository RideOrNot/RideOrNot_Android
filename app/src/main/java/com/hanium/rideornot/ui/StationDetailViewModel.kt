package com.hanium.rideornot.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.App.Companion.lineRepository
import com.hanium.rideornot.App.Companion.stationRepository
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.domain.*
import com.hanium.rideornot.repository.ArrivalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationDetailViewModel(context: Context, private val arrivalRepository: ArrivalRepository) : ViewModel() {

    private val _arrivalList = MutableLiveData<ArrivalResponse>()
    val arrivalList: LiveData<ArrivalResponse> = _arrivalList

    private val _lineList = MutableLiveData<List<Line>>()
    val lineList: LiveData<List<Line>> = _lineList

    private val _stationItem = MutableLiveData<Station>()
    val stationItem: LiveData<Station> = _stationItem


    // 해당 역, 호선의 도착 정보 얻기
    fun loadArrivalList(stationId: String, lineId: Int) {
        viewModelScope.launch {
            val arrivalList = arrivalRepository.getArrivalList(stationId, lineId)
            _arrivalList.value = arrivalList
        }
    }

    // 해당 역의 호선 목록 얻기
    fun loadLineList(stationName: String) {
        viewModelScope.launch {
            val lineId = withContext(Dispatchers.IO) { stationRepository.findLineByName(stationName) }
            val lineList = withContext(Dispatchers.IO) { lineRepository.getLinesByIds(lineId) as ArrayList<Line> }
            _lineList.value = lineList
        }
    }

    // 해당 역, 호선의 양옆 역 얻기
    fun loadNeighboringStation(stationName: String, lineId: Int) {
        viewModelScope.launch {
            val stationItem = withContext(Dispatchers.IO) { stationRepository.findNeighboringStation(stationName, lineId) }
            _stationItem.value = stationItem
        }
    }
}