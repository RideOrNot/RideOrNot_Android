package com.hanium.rideornot.data

import android.util.Log
import com.hanium.rideornot.ArrivalView
import com.hanium.rideornot.utils.NetworkModule.Companion.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArrivalService {
    private lateinit var arrivalView: ArrivalView

    fun setArrivalView(arrivalView: ArrivalView) {
        this.arrivalView = arrivalView
    }

    // 열차 도착 정보 조회
    fun getArrivalInfo(stationName: String) {
        val arrivalService = getRetrofit().create(ArrivalRetrofitInterface::class.java)
        arrivalService.getArrivalInfo(stationName).enqueue(object : Callback<ArrayList<ArrivalResponse>> {

            override fun onResponse(
                call: Call<ArrayList<ArrivalResponse>>,
                response: Response<ArrayList<ArrivalResponse>>
            ) {
                Log.d("도착 정보 API/SUCCESS", response.toString())

                arrivalView.onArrivalSuccess(response.body()!![0])
            }

            override fun onFailure(call: Call<ArrayList<ArrivalResponse>>, t: Throwable) {
                Log.d("도착 정보 API/FAILURE", t.message.toString())
            }
        })
    }
}