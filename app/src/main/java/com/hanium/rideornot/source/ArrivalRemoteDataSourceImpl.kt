package com.hanium.rideornot.source

import android.util.Log
import com.hanium.rideornot.data.ArrivalService
import com.hanium.rideornot.data.response.ArrivalResponse

class ArrivalRemoteDataSourceImpl(
    private val arrivalService: ArrivalService
) : ArrivalRemoteDataSource {
    override suspend fun getArrivalInfoList(stationId: String, lineId: Int): ArrivalResponse {
        return try {
            val response = arrivalService.getArrivalInfoList(stationId, lineId)
            if (response.resultCode == "SUCCESS") {
                response.result
            } else {
                Log.e("실패", "서버 요청 실패: ${response.resultCode}")
                throw IllegalStateException("서버 요청 실패: ${response.resultCode}")
            }
        } catch (e: Exception) {
            Log.e("에러", "서버 요청 중 예외 발생: ${e.message}")
            throw e
        }
    }
}