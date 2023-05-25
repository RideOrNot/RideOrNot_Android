package com.hanium.rideornot.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.R

object NotificationManager {

    private const val CHANNEL_ID = "ride_or_not_notification_push_channel"
    var index = 1

    fun createNotification(context: Context, notificationData: NotificationModel) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(context, notificationManager)

        notificationManager.notify(
            index, buildNotification(
                context, notificationData
            )
        )
    }

    private fun createNotificationChannel(
        context: Context,
        notificationManager: NotificationManager
    ) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.ride_or_not_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.ride_or_not_notification_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(
        context: Context,
        notificationData: NotificationModel
    ): Notification {

        // 푸시 알림 터치시 실행할 작업 설정 (여기선 MainActivity로 이동하도록 설정)
        val intent = Intent(context, MainActivity::class.java)
        // 푸시 알림을 터치하여 실행할 작업에 대한 Flag 설정 (현재 액티비티를 최상단으로 올림 | 최상단 액티비티를 제외하고 모든 액티비티를 제거)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "해제" 버튼을 눌렀을 때 실행될 BroadcastReceiver의 Intent 생성
        val dismissIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(notificationData.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.text))
            .setSmallIcon(R.drawable.ic_app_logo_round)
            .setColor(ContextCompat.getColor(context, R.color.blue))
            .addAction(R.drawable.ic_app_logo_round, "해제", dismissPendingIntent)
            .addAction(R.drawable.ic_app_logo_round, "확인", pendingIntent)
            .setAutoCancel(true)
            .build()
    }

}

