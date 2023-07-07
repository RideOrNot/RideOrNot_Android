package com.hanium.rideornot.data

data class ArrivalResponse(
    val arrivalTime: Int,
    val direction: String,
    val lineName: String,
    val destination: String
)