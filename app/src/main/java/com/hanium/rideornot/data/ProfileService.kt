package com.hanium.rideornot.data

import com.hanium.rideornot.data.request.ProfilePostRequestBody
import com.hanium.rideornot.data.response.BaseResponse
import com.hanium.rideornot.data.response.ProfileGetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ProfileService {
    //@Headers("Content-Type: application/json")
    @GET("/auths/profile")
    suspend fun getProfile(): BaseResponse<ProfileGetResponse>

    @Headers("Content-Type: application/json")
    @POST("auths/profile")
    suspend fun postProfile(@Body body: ProfilePostRequestBody) : Response<String>
}