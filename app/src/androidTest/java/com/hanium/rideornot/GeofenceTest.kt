package com.hanium.rideornot

import com.hanium.rideornot.gps.GpsManager
import org.junit.Before
import org.junit.Test
import com.hanium.rideornot.gps.GpsManager.initGpsManager
import androidx.test.core.app.ActivityScenario
import com.hanium.rideornot.gps.GpsManager.geofenceList
import com.hanium.rideornot.gps.GpsManager.logGeofenceList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
    fun geofenceTest() {
        val testCoroutineScope = CoroutineScope(Dispatchers.Main)
        testCoroutineScope.launch {
            async {
                GpsManager.addGeofence("a", 37.540455, 126.9700533, 1000f, 100000)
            }.await()
            logGeofenceList()

            async {
                GpsManager.addGeofence("b", 38.540455, 127.9700533, 1000f, 100000)
            }.await()
            logGeofenceList()

            val removeList: List<String> = listOf("a", "b")
            async {
                GpsManager.removeGeofences(removeList)
            }.await()
            logGeofenceList()
        }
    }


    @Test
    fun enterGeofence() {

    }

}