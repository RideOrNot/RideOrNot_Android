package com.hanium.rideornot.data

import androidx.room.Delete
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import com.hanium.rideornot.data.request.ProfilePostRequestBody
import com.hanium.rideornot.data.response.BaseResponse
import com.hanium.rideornot.data.response.ProfileGetResponse
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {
    //@Headers("Content-Type: application/json")
    @GET("/auths/profile")
    suspend fun getProfile(): BaseResponse<ProfileGetResponse>

    @Headers("Content-Type: application/json")
    @POST("auths/profile")
    suspend fun postProfile(@Body body: ProfilePostRequestBody) : Response<String>

    @DELETE("auths/profile/{uid}")
    suspend fun deleteUser(@Path("uid") uid: Int): Response<String>
}