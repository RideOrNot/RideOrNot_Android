package com.hanium.rideornot.data

import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.data.response.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ArrivalService {

    // 열차 도착 정보 조회
    @GET("/stations/{stationId}/{lineId}")
    suspend fun getArrivalInfoList(@Path("stationId") stationId: String, @Path("lineId") lineId: Int): BaseResponse<ArrivalResponse>

//    // 열차 도착 정보 조회
//    fun getArrivalInfo(stationName: String) {
//        val arrivalService = getRetrofit().create(ArrivalRetrofitInterface::class.java)
//        arrivalService.getArrivalInfo(stationName)
//            .enqueue(object : Callback<ArrayList<ArrivalResponse>> {
//
//                override fun onResponse(
//                    call: Call<ArrayList<ArrivalResponse>>,
//                    response: Response<ArrayList<ArrivalResponse>>
//                ) {
//                    Log.d("도착 정보 API/SUCCESS", response.toString())
//
//                    arrivalView.onArrivalSuccess(response.body()!!)
//                }
//
//                override fun onFailure(call: Call<ArrayList<ArrivalResponse>>, t: Throwable) {
//                    Log.d("도착 정보 API/FAILURE", t.message.toString())
//                }
//            })
//    }
}