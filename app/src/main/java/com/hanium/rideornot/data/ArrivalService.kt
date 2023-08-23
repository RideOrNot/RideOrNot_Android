package com.hanium.rideornot.data

import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.data.response.BaseResponse
import com.hanium.rideornot.data.response.RideNotificationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArrivalService {

    // 해당 역, 노선에 대한 도착 정보, 역사 혼잡도 조회
    @GET("/stations/{stationId}/{lineId}")
    suspend fun getArrivalList(
        @Path("stationId") stationId: String,
        @Path("lineId") lineId: Int
    ): BaseResponse<ArrivalResponse>

    // 해당 역에 대한 모든 도착 정보 조회
    @GET("/stations/arrivalInfo/{stationId}")
    suspend fun getArrivalListByStationId(
        @Path("stationId") stationId: String,
    ): BaseResponse<ArrivalResponse>

    // 실시간 승차 알림
    @GET("/stations/pushAlarm")
    suspend fun getRideNotification(
        @Query("stationName") stationName: String,
        @Query("exitName") exitName: String
        ): BaseResponse<RideNotificationResponse>
}