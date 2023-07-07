package com.hanium.rideornot.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ArrivalRetrofitInterface {

    // 열차 도착 정보 조회
    @GET("/test")
    fun getArrivalInfo(@Query("stationName") stationName: String): Call<ArrayList<ArrivalResponse>>
}