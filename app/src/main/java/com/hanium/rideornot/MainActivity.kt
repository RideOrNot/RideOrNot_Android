package com.hanium.rideornot

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsForegroundService
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.LOCATION_REQUEST_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.ui.dialog.PermissionInfoDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val PREFS_NAME = "MyPrefs"
    private val FIRST_RUN_KEY = "firstRun"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()

        val preferences: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true)

        if (isFirstRun) {
            // 최초 실행인 경우, 접근 권한 안내 창을 표시
            showPermissionInfoDialog()

            // 최초 실행 여부 업데이트
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(FIRST_RUN_KEY, false)
            editor.apply()
        } else {
            // 최초 실행이 아닌 경우
            GpsManager.initGpsManager(this)
        }


        if (GpsManager.arePermissionsGranted(this) && !isServiceRunning(GpsForegroundService::class.java)) {
            // 포그라운드 서비스 시작
            val serviceIntent = Intent(this, GpsForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        // 실시간 위치 업데이트 시작
        //GpsManager.startLocationUpdates()
        // 지오펜스 생성 예시
        //GpsManager.addGeofence("myStation", 37.540455,126.9700533 ,1000f, 1000000)

    }

    override fun onResume() {
        super.onResume()

        // 앱이 다시 포그라운드로 돌아왔을 때 위치 권한 체크
        if (!GpsManager.arePermissionsGranted(this)) {
            // 포그라운드 서비스 종료 요청
            val serviceIntent = Intent(this, GpsForegroundService::class.java)
            stopService(serviceIntent)
        }
    }

    private fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frm_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bnvMain.setupWithNavController(navController)
    }

    private fun showPermissionInfoDialog() {
        // 접근 권한 안내 Dialog 표시
        val dialog = PermissionInfoDialog(this)

        dialog.btnClickListener {
            // 권한 요청 수행
            GpsManager.initGpsManager(this)
        }
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_PERMISSION_REQUEST_CODE) {
            if (GpsManager.arePermissionsGranted(this)) {
                // 권한이 허용된 경우
                // 포그라운드 서비스 시작
                val serviceIntent = Intent(this, GpsForegroundService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                // 권한이 거부된 경우
                // 포그라운드 서비스가 진행 중이면 종료
                if (isServiceRunning(GpsForegroundService::class.java)) {
                    val serviceIntent = Intent(this, GpsForegroundService::class.java)
                    stopService(serviceIntent)
                }
                // 스낵바 표시
                Snackbar.make(
                    binding.root,
                    "위치 권한을 항상 허용해야 승차 알림 서비스를 사용할 수 있습니다.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    // 서비스 클래스가 실행 중인지 여부 확인
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.runningAppProcesses

        if (runningServices != null) {
            for (processInfo in runningServices) {
                // 현재 프로세스 이름과 원하는 서비스 클래스 이름이 일치하는지 확인
                if (processInfo.processName == serviceClass.name) {
                    return true  // 실행 중인 경우 true 반환
                }
            }
        }
        return false  // 실행 중이 아닌 경우 false 반환
    }


}
