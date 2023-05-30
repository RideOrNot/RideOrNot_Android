package com.hanium.rideornot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("GeofenceBroadcastReceiver", "onReceive")
        if (context == null || intent == null) {
            Log.e("GeofenceBroadcastReceiveFailure", "context or intent is null")
            return
        }
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e("GeofenceBroadcastReceiver", "intent: $intent")
            return
        }
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofencingEventError", errorMessage)
            return
        }
        // transition type을 get
        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.d("GeofenceBroadcastReceiver", "geofenceTransition: $geofenceTransition")

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            // 이벤트 트리거가 작동된 geofence들을 get
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
            )
            Log.d("GeofenceReceiver", "onReceive")
            if (triggeringGeofences != null) {
                for (i in triggeringGeofences) {
                    Log.d("triggeringGeofence", "${i.requestId}")
                }
            }
            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails)
            //Log.i("geofenceTransitionDetails", geofenceTransitionDetails)
        } else {
            // Log the error.
        }
    }

    private fun getGeofenceTransitionDetails(
        geofenceBroadcastReceiver: GeofenceBroadcastReceiver,
        @Geofence.GeofenceTransition geofenceTransition: Int,
        triggeringGeofences: List<Geofence>?
    ) {

    }

    private fun sendNotification() {

    }

}