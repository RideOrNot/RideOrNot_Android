package com.hanium.rideornot.data.response

data class BaseResponse<T>(
    val resultCode: String,
    val result: T
)