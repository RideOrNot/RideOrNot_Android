package com.hanium.rideornot.data

import com.hanium.rideornot.data.request.SignInRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("/auths/sign-in")
    suspend fun postGoogleIdToken(
        @Body body: SignInRequest
    ): Response<String>
}