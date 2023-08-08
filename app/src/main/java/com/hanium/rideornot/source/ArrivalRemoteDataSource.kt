package com.hanium.rideornot.source

import com.hanium.rideornot.data.response.ArrivalResponse

interface ArrivalRemoteDataSource {
    suspend fun getArrivalList(stationId: String, lineId: Int): ArrivalResponse

    suspend fun getArrivalListByStationId(stationId: String): ArrivalResponse
}