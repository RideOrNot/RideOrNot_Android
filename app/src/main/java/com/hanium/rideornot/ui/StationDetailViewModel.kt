package com.hanium.rideornot.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.domain.Line
import com.hanium.rideornot.domain.LineDao
import com.hanium.rideornot.domain.StationDao
import com.hanium.rideornot.domain.StationDatabase
import com.hanium.rideornot.repository.ArrivalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationDetailViewModel(context: Context, private val arrivalRepository: ArrivalRepository) : ViewModel() {

    private val _arrivalInfoList = MutableLiveData<ArrivalResponse>()
    val arrivalInfoList: LiveData<ArrivalResponse> = _arrivalInfoList

    private val _lineList = MutableLiveData<List<Line>>()
    val lineList: LiveData<List<Line>> = _lineList

    private val database = StationDatabase.getInstance(context)
    private var stationDao: StationDao = database!!.stationDao()
    private var lineDao: LineDao = database!!.lineDao()


//    init {
//        loadArrivalInfo(stationName)
//    }

    fun loadArrivalInfo(stationId: String, lineId: Int) {
        viewModelScope.launch {
            val arrivalInfoList = arrivalRepository.getArrivalInfoList(stationId, lineId)
            _arrivalInfoList.value = arrivalInfoList
        }
    }

    // 해당 역의 호선 목록 얻기
    fun loadLineList(stationName: String) {
        viewModelScope.launch {
            val lineId = withContext(Dispatchers.IO) { stationDao.findLineByName(stationName) }
            val lineList = withContext(Dispatchers.IO) { lineDao.getLinesByIds(lineId) as ArrayList<Line> }
            _lineList.value = lineList
        }
    }

}