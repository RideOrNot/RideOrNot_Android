package com.hanium.rideornot.repository

import androidx.lifecycle.LiveData
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.SearchHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope


class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {
    val getAllData: LiveData<List<SearchHistory>> = searchHistoryDao.getAllData()

    suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.insertData(searchHistory)
    }

    suspend fun deleteSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.deleteData(searchHistory)
    }

}