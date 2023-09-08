package com.hanium.rideornot.notification

import android.Manifest
import android.app.*
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.MainActivity.Companion.moveAppSettings
import com.hanium.rideornot.R
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.LOCATION_PERMISSION_REQUEST_CODE

const val NOTIFICATION_PERMISSION_REQUEST_CODE: Int = 2

object NotificationManager {

    private const val CHANNEL_ID = "ride_or_not_notification_push_channel"
    var index = 1

    fun initNotificationManager(activity: MainActivity) {
        // 알림 권한 확인
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Android 13(SDK 33) 이상은 알림 권한 요청 필요
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                if (GpsManager.arePermissionsGranted(activity)) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("서비스 이용 알림").setCancelable(false)
                    builder.setMessage("앱을 사용하기 위해서는 알림 권한이 필요합니다. 설정으로 이동하여 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(activity, NOTIFICATION_PERMISSION_REQUEST_CODE)
                    }
                    builder.show()
                } else {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("서비스 이용 알림").setCancelable(false)
                    builder.setMessage("앱을 사용하기 위해서는 위치, 알림 권한이 필요합니다. 설정으로 이동하여 권한을 항상 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(activity, LOCATION_PERMISSION_REQUEST_CODE)
                    }
                    builder.show()
                }
            }
        }

    }

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

    fun updateNotificationContent(context: Context, notificationId: Int, updatedNotificationData: NotificationModel) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 기존 알림을 가져오기
        val existingNotification = notificationManager.activeNotifications
            .firstOrNull { it.id == notificationId }?.notification

        if (existingNotification != null) {
            // 새로운 알림 내용을 사용하여 기존 알림 업데이트
            val updatedNotification = buildUpdatedNotification(context, existingNotification, updatedNotificationData)

            // 기존 알림을 업데이트
            notificationManager.notify(notificationId, updatedNotification)
        }
    }

    // 기존 알림의 내용을 수정하고 업데이트된 알림을 반환하는 함수
    private fun buildUpdatedNotification(context: Context, existingNotification: Notification, updatedNotificationData: NotificationModel): Notification {
        // 기존 알림의 내용 가져오기
        val notificationContent = NotificationCompat.getExtras(existingNotification)

        // 업데이트된 내용으로 수정
        notificationContent?.apply {
            putString(NotificationCompat.EXTRA_TITLE, updatedNotificationData.title)
            putString(NotificationCompat.EXTRA_TEXT, updatedNotificationData.text.joinToString("\n"))
            // 기타 업데이트할 내용
        }

        // 수정된 내용으로 알림 빌드
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(updatedNotificationData.title)
            .setContentText(updatedNotificationData.text.joinToString("\n"))
            .setSmallIcon(R.drawable.ic_app_logo_round)

        // 수정된 내용을 알림에 적용
        builder.setExtras(notificationContent)

        return builder.build()
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


        val title = notificationData.title
        val stationName = notificationData.stationName
        val notificationText = notificationData.text.joinToString("\n")

        // 글씨 굵기 조절
//        val sb = SpannableStringBuilder()
//        val boldStart = sb.length
//        sb.append(stationName)
//        val boldEnd = sb.length
//        sb.setSpan(StyleSpan(Typeface.BOLD), boldStart, boldEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
////        sb.append("\n")
////        sb.append(notificationText)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle("$stationName $title")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setSmallIcon(R.drawable.ic_app_logo_round)
            .setColor(ContextCompat.getColor(context, R.color.blue))
            .addAction(R.drawable.ic_app_logo_round, "해제", dismissPendingIntent)
            .setAutoCancel(true)
            .build()
    }

    // 알림 권한 허용 여부를 확인
    fun isPermissionGranted(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

}

