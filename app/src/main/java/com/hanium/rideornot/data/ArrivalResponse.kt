package com.hanium.rideornot.data

data class ArrivalResponse(
    var arrivalTime: Int,
    var direction: String,
    var lineName: String,
    var destination: String
)