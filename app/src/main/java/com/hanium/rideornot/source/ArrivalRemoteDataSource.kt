package com.hanium.rideornot.source

import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.data.response.RideNotificationResponse

interface ArrivalRemoteDataSource {
    suspend fun getArrivalList(stationId: String, lineId: Int): ArrivalResponse

    suspend fun getArrivalListByStationId(stationId: String): ArrivalResponse

    suspend fun getRideNotification(stationName: String, exitName: String): RideNotificationResponse
}