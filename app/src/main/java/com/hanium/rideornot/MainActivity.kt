package com.hanium.rideornot


import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.*
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsManager


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
        //GpsManager.startLocationUpdates()


        GpsManager.addGeofence("1", 37.540455,126.9700533 ,1000f, 1000000)
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
