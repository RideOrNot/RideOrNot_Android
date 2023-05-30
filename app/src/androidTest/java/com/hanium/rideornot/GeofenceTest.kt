package com.hanium.rideornot

import com.hanium.rideornot.gps.GpsManager
import org.junit.Before
import org.junit.Test
import com.hanium.rideornot.gps.GpsManager.initGpsManager
import androidx.test.core.app.ActivityScenario

class GeofenceTest {

    private lateinit var mainActivity: MainActivity


    @Before
    fun setUp() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            mainActivity = it
        }
        initGpsManager(mainActivity)
    }

    @Test
    fun addGeofence() {
        GpsManager.addGeofence("1", 37.540455, 126.9700533,10f, 100000)

    }

    @Test
    fun enterGeofence() {

    }

}