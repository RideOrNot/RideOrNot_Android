package com.hanium.rideornot.source

import com.hanium.rideornot.data.response.ArrivalResponse

interface ArrivalRemoteDataSource {
    suspend fun getArrivalList(stationId: String, lineId: Int) : ArrivalResponse
}