package com.hanium.rideornot.data

import com.hanium.rideornot.data.response.BaseResponse
import com.hanium.rideornot.data.response.SignInResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

interface AuthService {
    @POST("/login")
    suspend fun postGoogleIdToken(
        @Field("googleIdToken") googleIdToken: String
    ): BaseResponse<SignInResponse>
}