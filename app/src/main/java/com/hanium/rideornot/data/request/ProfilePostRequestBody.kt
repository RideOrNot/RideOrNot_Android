package com.hanium.rideornot.data.request

data class ProfilePostRequestBody(
    val ageRange: Int,
    val gender: Int,
    val nickName: String
)