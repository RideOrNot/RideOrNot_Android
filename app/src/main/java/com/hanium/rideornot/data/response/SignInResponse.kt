package com.hanium.rideornot.data.response

data class SignInResponse(
    val isNewUser : Boolean,
    val jwt: String
)
