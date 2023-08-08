package com.hanium.rideornot.ui.search

import com.hanium.rideornot.domain.Station


interface ISearchHistoryRV {
    fun onSearchHistoryItemDeleteClick(position: Int)
    fun onSearchHistoryItemClick(stationName: String)
}