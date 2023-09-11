package com.hanium.rideornot.data

import com.hanium.rideornot.data.response.BaseResponse
import com.hanium.rideornot.data.response.ProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ProfileService {
    //@Headers("Content-Type: application/json")
    @GET("/auths/profile")
    suspend fun getProfile(): BaseResponse<ProfileDto>

    @Headers("Content-Type: application/json")
    @POST("auths/profile")
    suspend fun postProfile(@Body body: ProfileDto) : Response<String>
}