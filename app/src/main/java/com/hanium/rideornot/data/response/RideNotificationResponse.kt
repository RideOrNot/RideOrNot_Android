package com.hanium.rideornot.data.response

import com.google.gson.annotations.SerializedName

data class RideNotificationResponse(
    @SerializedName("arrivalInfo") var rideNotificationList: List<RideNotification>
)

data class RideNotification(
    var arrivalTime: Int,
    var movingTime: Int,
    var direction: String,
    var lineId: String,
    var destination: String,
    var message: String,
    var movingSpeedStep: Int,
    var movingSpeed: Double,
    var stationName: String,
    var arrivalMessage2: String,
    var arrivalMessage3: String
)