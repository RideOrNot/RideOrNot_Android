package com.hanium.rideornot

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsForegroundService
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.LOCATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.notification.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.ui.dialog.PermissionInfoDialog
import com.hanium.rideornot.ui.home.HomeFragment

private const val PREFS_NAME = "mainActivity"
private const val FIRST_RUN_KEY = "firstRun"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionDialog: PermissionInfoDialog

    companion object {
        // 앱 설정 화면으로 이동
        fun moveAppSettings(mainActivity: MainActivity, requestCode: Int) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", mainActivity.packageName, null)
            intent.data = uri

            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)
                mainActivity.moveLocationSettingsLauncher.launch(intent)
            else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE)
                mainActivity.moveNotificationSettingsLauncher.launch(intent)
        }
    }

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
            val editor = preferences.edit()
            editor.putBoolean(FIRST_RUN_KEY, false)
            editor.apply()
        }
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
        permissionDialog = PermissionInfoDialog(this)

        permissionDialog.btnClickListener {
            // 권한 요청 수행
            GpsManager.initGpsManager(this)
        }
        permissionDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (GpsManager.arePermissionsGranted(this)) {
                // 권한이 허용된 경우
                if (NotificationManager.isPermissionGranted(this) && HomeFragment.switchRideChecked) {
                    // 포그라운드 서비스 시작
                    val serviceIntent = Intent(this, GpsForegroundService::class.java)
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    NotificationManager.initNotificationManager(this)
                }
            } else {
                // 권한이 거부된 경우
                // 포그라운드 서비스가 진행 중이면 종료
                if (GpsForegroundService.isServiceRunning) {
                    val serviceIntent = Intent(this, GpsForegroundService::class.java)
                    stopService(serviceIntent)
                }

                if (NotificationManager.isPermissionGranted(this)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("서비스 이용 알림").setCancelable(false)
                    builder.setMessage("앱을 사용하기 위해서는 위치 권한이 필요합니다. 설정으로 이동하여 권한을 항상 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(this, requestCode)
                    }
                    builder.show()
                } else {
                    NotificationManager.initNotificationManager(this)
                }
            }
        } else if(requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (NotificationManager.isPermissionGranted(this)) {
                // 권한이 허용된 경우
                if (GpsManager.arePermissionsGranted(this) && HomeFragment.switchRideChecked) {
                    // 포그라운드 서비스 시작
                    val serviceIntent = Intent(this, GpsForegroundService::class.java)
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("서비스 이용 알림").setCancelable(false)
                    builder.setMessage("앱을 사용하기 위해서는 위치 권한이 필요합니다. 설정으로 이동하여 권한을 항상 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(this, LOCATION_PERMISSION_REQUEST_CODE)
                    }
                    builder.show()
                }
            } else {
                // 권한이 거부된 경우
                // 포그라운드 서비스가 진행 중이면 종료
                if (GpsForegroundService.isServiceRunning) {
                    val serviceIntent = Intent(this, GpsForegroundService::class.java)
                    stopService(serviceIntent)
                }
                if (GpsManager.arePermissionsGranted(this)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("서비스 이용 알림").setCancelable(false)
                    builder.setMessage("앱을 사용하기 위해서는 알림 권한이 필요합니다. 설정으로 이동하여 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(this, requestCode)
                    }
                    builder.show()
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("서비스 이용 알림").setCancelable(false)
                    builder.setMessage("앱을 사용하기 위해서는 위치, 알림 권한이 필요합니다. 설정으로 이동하여 권한을 항상 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(this, requestCode)
                    }
                    builder.show()
                }

            }
        }
    }

    private val moveLocationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 설정 화면에서 돌아왔을 때의 처리
            GpsManager.initGpsManager(this)

            if (GpsManager.arePermissionsGranted(this) && NotificationManager.isPermissionGranted(this) && !GpsForegroundService.isServiceRunning && HomeFragment.switchRideChecked) {
                // 포그라운드 서비스 시작
                val serviceIntent = Intent(this, GpsForegroundService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }

    private val moveNotificationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 설정 화면에서 돌아왔을 때의 처리
            NotificationManager.initNotificationManager(this)

            if (GpsManager.arePermissionsGranted(this) && NotificationManager.isPermissionGranted(this) && !GpsForegroundService.isServiceRunning && HomeFragment.switchRideChecked) {
                // 포그라운드 서비스 재시작
                val serviceIntent = Intent(this, GpsForegroundService::class.java)
                stopService(serviceIntent)
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }

}
