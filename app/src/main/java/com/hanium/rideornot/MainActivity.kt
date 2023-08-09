package com.hanium.rideornot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

    }

    private fun initBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frm_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bnvMain.setupWithNavController(navController)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .build()

        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.fragment_home, null, navOptions)
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    navController.navigate(R.id.fragment_search, null, navOptions)
                    return@setOnItemSelectedListener true
                }
                R.id.favoriteFragment -> {
                    navController.navigate(R.id.fragment_favorite, null, navOptions)
                    return@setOnItemSelectedListener true
                }
                R.id.settingFragment -> {
                    navController.navigate(R.id.fragment_setting, null, navOptions)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

}
