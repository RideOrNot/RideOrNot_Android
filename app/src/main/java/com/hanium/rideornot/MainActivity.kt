package com.hanium.rideornot

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import android.app.AlertDialog
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsForegroundService
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.LOCATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.notification.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.ui.signUp.SignUpFragment1
import com.hanium.rideornot.ui.dialog.PermissionInfoDialog
import com.hanium.rideornot.utils.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val REQ_ONE_TAP = 2
private const val PREFS_NAME = "mainActivity"
private const val FIRST_RUN_KEY = "firstRun"
private const val JWT_KEY = "jwt"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private var showOneTapUi: Boolean = true
    private val loginResultHandler = registerForActivityResult<IntentSenderRequest, ActivityResult>(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) Log.d("loginResultHandler", "RESULT_OK.")
        if (result.resultCode == RESULT_CANCELED) Log.d("loginResultHandler", "RESULT_CANCELED.")
        if (result.resultCode == RESULT_FIRST_USER) Log.d("loginResultHandler", "RESULT_FIRST_USER.")
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val username = credential.id
            val password = credential.password
            if (idToken != null) {
                Log.d("loginResultHandler", "Got ID token, $idToken")
            }
            if (password != null) {
                Log.d("loginResultHandler", "Got password., $password")
            }
            if (username != null) {
                Log.d("loginResultHandler", "Got username, $username")
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d("loginResultHandler", "One-tap dialog was closed.")
                    // Don't re-prompt the user.
                    showOneTapUi = false
                }
                CommonStatusCodes.NETWORK_ERROR -> Log.d("loginResultHandler", "One-tap encountered a network error.")
                else -> Log.d(
                    "loginResultHandler", "Couldn't get credential from result."
                            + e.localizedMessage
                )
            }
        }
    }

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
        } else {
            // 최초 실행이 아닌 경우
            GpsManager.initGpsManager(this)
        }

        if (GpsManager.arePermissionsGranted(this) && NotificationManager.isPermissionGranted(this) && !GpsForegroundService.isServiceRunning) {
            // 포그라운드 서비스 시작
            val serviceIntent = Intent(this, GpsForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        // 실시간 위치 업데이트 시작
        //GpsManager.startLocationUpdates()
        // 지오펜스 생성 예시
        //GpsManager.addGeofence("myStation", 37.540455,126.9700533 ,1000f, 1000000)

        Log.d("FragmentManager", "replace fragment to SignInFragment")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frm_main, SignUpFragment1())
        transaction.commit()

        val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(googleWebClientId)
                    // Only show accounts previously used to sign in.
                    // 첫 로그인 시 false로, 로그인 정보가 있을 땐 true로 설정하기
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("oneTapUiFailure", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d("beginSignInFailure", e.localizedMessage)
            }


    }

    // TODO: deprecated 메서드인 onActivityResult -> startActivityForResult로 변경하기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    Log.d("loginResultHandler", "method operated")
                    if (idToken != null) {
                        Log.d("loginResultHandler", "Got ID token, $idToken")
                        CoroutineScope(Dispatchers.Main).launch {
                            val response = NetworkModule.getAuthService().postGoogleIdToken(idToken)
                            val preferences: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            preferences.edit().putString(JWT_KEY, response.result.jwt).apply()
                            // 새 유저일 경우
                            if (response.result.isNewUser) {
                                TODO("SignUpFragment로 이동")
                            }
                        }
                    }
                    if (password != null) {
                        Log.d("loginResultHandler", "Got password., $password")
                    }
                    if (username != null) {
                        Log.d("loginResultHandler", "Got username, $username")
                    }

                } catch (e: ApiException) {
                    Log.e("loginResultHandler", e.toString())
                    // ...
                }
            }
        }
    }
    // ...


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
//            NotificationManager.initNotificationManager(this)
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
                if (NotificationManager.isPermissionGranted(this)) {
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
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (NotificationManager.isPermissionGranted(this)) {
                // 권한이 허용된 경우
                if (GpsManager.arePermissionsGranted(this)) {
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

            if (GpsManager.arePermissionsGranted(this) && NotificationManager.isPermissionGranted(this) && !GpsForegroundService.isServiceRunning) {
                // 포그라운드 서비스 시작
                val serviceIntent = Intent(this, GpsForegroundService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }

    private val moveNotificationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 설정 화면에서 돌아왔을 때의 처리
            NotificationManager.initNotificationManager(this)

            if (GpsManager.arePermissionsGranted(this) && NotificationManager.isPermissionGranted(this) && !GpsForegroundService.isServiceRunning) {
                // 포그라운드 서비스 재시작
                val serviceIntent = Intent(this, GpsForegroundService::class.java)
                stopService(serviceIntent)
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }

}
