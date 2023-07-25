package com.hanium.rideornot.source

import com.hanium.rideornot.data.response.ArrivalResponse

interface ArrivalRemoteDataSource {
    suspend fun getArrivalInfoList(stationId: String, lineId: Int) : ArrivalResponse
}