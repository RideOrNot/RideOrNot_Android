package com.hanium.rideornot.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.SearchHistoryDatabase
import com.hanium.rideornot.repository.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchHistoryViewModel(context: Context) : ViewModel() {

    val searchHistoryList: LiveData<List<SearchHistory>>
    private val repository: SearchHistoryRepository

    init {
        val searchHistoryDao = SearchHistoryDatabase.getInstance(context)!!.searchHistoryDao()
        repository = SearchHistoryRepository(searchHistoryDao)
        searchHistoryList = repository.getAllData
    }

    fun insertSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSearchHistory(searchHistory)
        }
    }

    fun deleteSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSearchHistory(searchHistory)
        }
    }

}