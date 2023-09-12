package com.hanium.rideornot.data.response


data class ProfileGetResponse(
    val id: Int,
    var ageRange: Int,
    var gender: Int,
    val nickName: String,
    var email: String
)