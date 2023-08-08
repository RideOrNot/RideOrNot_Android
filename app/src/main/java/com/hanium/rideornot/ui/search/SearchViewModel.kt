package com.hanium.rideornot.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.domain.*
import com.hanium.rideornot.repository.SearchHistoryRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SearchViewModel(context: Context) : ViewModel() {

    val searchHistoryList: LiveData<List<SearchHistory>>
    private val searchHistoryRepository: SearchHistoryRepository
    private val stationDao : StationDao

    init {
        val searchHistoryDao = SearchHistoryDatabase.getInstance(context)!!.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
        stationDao = StationDatabase.getInstance(context)!!.stationDao()
        searchHistoryList = searchHistoryRepository.getAllData
    }

    suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.insertSearchHistory(searchHistory)
        }
    }

    suspend fun deleteSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.deleteSearchHistory(searchHistory)
        }
    }

    suspend fun findLineByName(stationName: String) : Deferred<List<Int>> {
        return viewModelScope.async(Dispatchers.IO) {
            return@async stationDao.findLineByName(stationName)
        }
    }
}