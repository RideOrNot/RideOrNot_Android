package com.hanium.rideornot.repository

import androidx.lifecycle.LiveData
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.SearchHistoryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {
    val getAllData: LiveData<List<SearchHistory>> = searchHistoryDao.getAllData()

    suspend fun getSearchHistory(stationName: String) : SearchHistory? {
        return withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
            searchHistoryDao.getDataByStationName(stationName)
        }
    }

    fun insertSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.insertData(searchHistory)
    }

    fun deleteSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.deleteData(searchHistory)
    }

}