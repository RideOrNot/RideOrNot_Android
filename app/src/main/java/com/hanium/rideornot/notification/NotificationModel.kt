package com.hanium.rideornot.notification

data class NotificationModel(
    val id: Long,
    val contentType: ContentType,
    val contentId: Long,
    val stationName: String,
    val title: String,
    val text: ArrayList<String>
)

enum class ContentType {
    RIDE,  // 승차
    STATUS  // 탑승 여부
}