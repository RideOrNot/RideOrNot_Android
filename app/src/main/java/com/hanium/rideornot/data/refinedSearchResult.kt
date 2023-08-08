package com.hanium.rideornot.data

import com.hanium.rideornot.domain.Line

data class refinedSearchResult(
    val stationId: Int,
    val stationName: String,
    val lineList: List<Line>
)

