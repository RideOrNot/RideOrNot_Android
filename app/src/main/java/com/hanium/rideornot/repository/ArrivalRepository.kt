package com.hanium.rideornot.repository

import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.data.response.RideNotificationResponse
import com.hanium.rideornot.source.ArrivalRemoteDataSource

class ArrivalRepository(private val remoteDataSource: ArrivalRemoteDataSource) {
    suspend fun getArrivalList(stationId: String, lineId: Int): ArrivalResponse {
        return remoteDataSource.getArrivalList(stationId, lineId)
    }

    suspend fun getArrivalListByStationId(stationId: String): ArrivalResponse {
        return remoteDataSource.getArrivalListByStationId(stationId)
    }

    suspend fun getRideNotification(stationName: String, exitName: String): RideNotificationResponse {
        return remoteDataSource.getRideNotification(stationName, exitName)
    }
}