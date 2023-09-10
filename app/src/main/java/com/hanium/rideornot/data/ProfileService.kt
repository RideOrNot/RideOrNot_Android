package com.hanium.rideornot.data

import com.hanium.rideornot.data.request.SignInRequest
import com.hanium.rideornot.data.response.BaseResponse
import com.hanium.rideornot.data.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers

interface ProfileService {
    //@Headers("Content-Type: application/json")
    @GET("/auths/profile")
    suspend fun getProfile(
    ): BaseResponse<ProfileResponse>
}