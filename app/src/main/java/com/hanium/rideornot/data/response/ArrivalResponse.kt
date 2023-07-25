package com.hanium.rideornot.data.response

import com.google.gson.annotations.SerializedName

data class ArrivalResponse(
    @SerializedName("arrivalInfo") var arrivalList: List<Arrival>,
    var congestion: Double,
    var currentTime: String
)

data class Arrival(
    var arrivalTime: Int,
    var direction: String,
    var lineId: String,
    var destination: String
)