package com.hanium.rideornot.ui.search

import com.hanium.rideornot.domain.Station


interface ISearchResultRV {
    fun onSearchResultItemClick(station: Station)
}