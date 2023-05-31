package com.hanium.rideornot.notification

data class NotificationModel(
    val id: Long,
    val contentType: ContentType,
    val contentId: Long,
    val title: String,
    val text: String
)

enum class ContentType {
    RIDE
}
