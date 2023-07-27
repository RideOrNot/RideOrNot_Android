package com.hanium.rideornot.repository

import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.source.ArrivalRemoteDataSource

class ArrivalRepository(private val remoteDataSource: ArrivalRemoteDataSource) {
    suspend fun getArrivalList(stationId: String, lineId: Int): ArrivalResponse {
        return remoteDataSource.getArrivalList(stationId, lineId)
    }
}