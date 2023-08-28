package com.hanium.rideornot.utils

import com.hanium.rideornot.BuildConfig.BASE_URL
import com.hanium.rideornot.data.ArrivalService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    fun getArrivalService(
    ): ArrivalService {
        return retrofit.create(ArrivalService::class.java)
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}