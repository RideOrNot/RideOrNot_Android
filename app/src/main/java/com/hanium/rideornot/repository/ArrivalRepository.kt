package com.hanium.rideornot.repository

import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.source.ArrivalRemoteDataSource

class ArrivalRepository(private val remoteDataSource: ArrivalRemoteDataSource) {
    suspend fun getArrivalInfoList(stationId: String, lineId: Int): ArrivalResponse {
        return remoteDataSource.getArrivalInfoList(stationId, lineId)
    }
}