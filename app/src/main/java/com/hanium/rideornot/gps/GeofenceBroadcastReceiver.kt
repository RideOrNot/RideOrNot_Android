package com.hanium.rideornot.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.hanium.rideornot.notification.ContentType
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.notification.NotificationModel

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("GeofenceBroadcastReceiver", "onReceive")
        if (context == null || intent == null) {
            Log.e("GeofenceBroadcastReceiver", "passed context or intent is null")
            return
        }
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e("GeofenceBroadcastReceiver", "geofencingEvent is null, passed intent: $intent")
            return
        }
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofenceBroadcastReceiver", errorMessage)
            return
        }

        // 이벤트 트리거가 작동된 geofence들을 get
        val triggeringGeofences: MutableList<Geofence>? = geofencingEvent.triggeringGeofences
        if (triggeringGeofences == null) {
            // geofencingEvent로 가져온 트리거된 지오펜스 리스트가 null일 경우 (정상적인 경우는 아님)
            Log.e("GeofencingBroadcastReceiver", "triggeringGeofences are null")
            return
        }

        // transition type을 get
        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.d("GeofenceBroadcastReceiver", "geofenceTransition: $geofenceTransition")

        // Get the transition details as a String.
        val geofenceTransitionDetails = getGeofenceTransitionDetails(
            this,
            geofenceTransition,
            triggeringGeofences
        )

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                var j = 1
                for (i in triggeringGeofences) {
                    Log.d("GeofenceBroadcastReceiver", "triggeringGeofences: $j - ${i.requestId}")
                    j++
                }

                NotificationManager.createNotification(
                    context,
                    NotificationModel(
                        1,
                        ContentType.RIDE,
                        1,
                        "지오펜싱 테스트 알림",
                        "지오펜스에 들어왔습니다, requestId: "
                    )
                )

            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                var j = 1
                for (i in triggeringGeofences) {
                    Log.d("GeofenceBroadcastReceiver", "triggeringGeofences: $j - ${i.requestId}")
                    j++
                }

                NotificationManager.createNotification(
                    context,
                    NotificationModel(
                        1,
                        ContentType.RIDE,
                        1,
                        "지오펜싱 테스트 알림",
                        "지오펜스에서 빠져나왔습니다, requestId: "
                    )
                )
            }
            else -> {
                // 트리거된 지오펜싱 트리거가 Enter || Exit 이 아닐 경우 (이 또한 정상적인 경우는 아님)
                // Log the error.

            }
        }

    }

    private fun getGeofenceTransitionDetails(
        geofenceBroadcastReceiver: GeofenceBroadcastReceiver,
        @Geofence.GeofenceTransition geofenceTransition: Int,
        triggeringGeofences: List<Geofence>?
    ) {

    }


}