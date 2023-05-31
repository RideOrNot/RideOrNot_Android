package com.hanium.rideornot


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.*
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.GpsManager.logGeofenceList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // private val REQUEST_PERMISSION_LOCATION = 10

    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = MutableList<Geofence?>(100) { null }

    private var locationUpdateInterval: Long = 2000;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()

        GpsManager.initGpsManager(this)

        // 실시간 위치 업데이트 시작
        //GpsManager.startLocationUpdates()
        // 지오펜스 생성 예시
        //GpsManager.addGeofence("myStation", 37.540455,126.9700533 ,1000f, 1000000)

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

//            val removeList: List<String> = listOf("a", "b")
//            async {
//                GpsManager.removeGeofences(removeList)
//            }.await()
//            logGeofenceList()
        }
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frm_main, HomeFragment())
            .commitAllowingStateLoss()
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.favoriteFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, FavoriteFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.settingFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_main, SettingFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

}
