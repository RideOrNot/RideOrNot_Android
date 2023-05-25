package com.hanium.rideornot.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 알림을 종료하는 동작을 수행
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.cancel(NotificationManager.index)
    }
}