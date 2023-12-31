package com.hanium.rideornot.ui.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.domain.*
import com.hanium.rideornot.repository.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SearchViewModel(context: Context) : ViewModel() {

    val searchHistoryList: LiveData<List<SearchHistory>>
    private val searchHistoryRepository: SearchHistoryRepository
    private val stationDao : StationDao
    private val lineDao : LineDao

    init {
        val searchHistoryDao = SearchHistoryDatabase.getInstance(context)!!.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
        stationDao = StationDatabase.getInstance(context)!!.stationDao()
        lineDao = StationDatabase.getInstance(context)!!.lineDao()
        searchHistoryList = searchHistoryRepository.getAllData
    }

    fun insertSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingHistory = searchHistoryRepository.getSearchHistory(searchHistory.stationName)
            if (existingHistory != null) {
                searchHistoryRepository.deleteSearchHistory(existingHistory)
                searchHistoryRepository.insertSearchHistory(searchHistory)
            } else {
                searchHistoryRepository.insertSearchHistory(searchHistory)
            }
        }
    }

    fun deleteSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.async(Dispatchers.IO) {
            searchHistoryRepository.deleteSearchHistory(searchHistory)
        }
    }

    suspend fun findLinesByStationName(stationName: String) : List<Int> {
        return viewModelScope.async(Dispatchers.IO) {
            return@async stationDao.findLineByName(stationName)
        }.await()
    }

    suspend fun getLineNameByLineId(lineId: Int) : String{
        return viewModelScope.async(Dispatchers.IO) {
            return@async lineDao.getLineNameById(lineId)
        }.await()
    }

}