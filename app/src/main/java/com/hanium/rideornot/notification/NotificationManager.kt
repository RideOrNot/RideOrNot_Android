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
import com.hanium.rideornot.App
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.MainActivity.Companion.moveAppSettings
import com.hanium.rideornot.R
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.LOCATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.utils.PreferenceUtil

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
                    builder.setMessage("이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
                            + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
                            + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
                            + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
                            + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
                            + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(activity, LOCATION_PERMISSION_REQUEST_CODE)
                    }
                    builder.show()
                }
            }
        }
    }

    fun createNotification(context: Context, notificationData: NotificationModel) {
        if (!App.prefUtil.prefs.getBoolean(PreferenceUtil.PUSH_NOTIFICATION_KEY, true))
            return

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

