package com.hanium.rideornot

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.CarrierConfigManager.Gps
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.hanium.rideornot.data.request.SignInRequestBody
import com.hanium.rideornot.data.response.ProfileGetResponse
import com.hanium.rideornot.databinding.ActivityMainBinding
import com.hanium.rideornot.gps.GpsForegroundService
import com.hanium.rideornot.gps.GpsManager
import com.hanium.rideornot.gps.LOCATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.notification.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.hanium.rideornot.notification.NotificationManager
import com.hanium.rideornot.ui.dialog.PermissionInfoDialog
import com.hanium.rideornot.ui.setting.SettingViewModel
import com.hanium.rideornot.ui.signUp.SignUpFragment1
import com.hanium.rideornot.ui.signUp.SignUpViewModel
import com.hanium.rideornot.utils.NetworkModule
import com.hanium.rideornot.utils.PreferenceUtil
import kotlinx.coroutines.*


private const val REQ_ONE_TAP = 2
private const val PREFS_NAME = "mainActivity"
private const val FIRST_RUN_KEY = "firstRun"

private const val AUTH_PREFS_NAME = "auth"
private const val JWT_KEY = "jwt"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var signUpViewModel: SignUpViewModel

    //    private lateinit var settingViewModel: SettingViewModel
    private lateinit var preferenceUtil: PreferenceUtil

    // 안드로이드 기기의 API 레벨(31 이하?)이 낮을 경우 원탭로그인이 동작하지 않음.
    // missing feature{name=auth_api_credentials_begin_sign_in, version=8}
    // TODO: 원탭로그인이 동작하지 않는 예외상황을 고려하여 설정에서 따로 로그인할 수 있게 만들기
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
//                Log.d("loginResultHandler", "Got ID token, $idToken")
            }
            if (password != null) {
//                Log.d("loginResultHandler", "Got password., $password")
            }
            if (username != null) {
//                Log.d("loginResultHandler", "Got username, $username")
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

//        throw java.lang.IllegalArgumentException("test for DefaultUncaughtExceptionHandler")

        initBottomNavigation()

//        preferenceUtil = PreferenceUtil(this)
//        preferenceUtil.setJwt("")
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

//        Log.d("FragmentManager", "replace fragment to SignInFragment")
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.frm_main, SignUpFragment1())
//            .commit()
        startSignIn()
    }

    fun startSignIn() {
        CoroutineScope(Dispatchers.Main).launch {
            // 현재 jwt 만료 시 서버에서 exception 반환이 아닌, result에 null을 할당하여 반환하도록
            // 되어 있음. (resultCode도 SUCCESS로 나옴)
            // TODO: 서버측에 jwt 만료시 exception 반환 요청하고 구현 수정하기
            val response = withContext(Dispatchers.Default) {
                NetworkModule.getProfileService().getProfile()
            }

            Log.d("responseProfileGet", response.toString())
            // jwt가 만료되었을 시 혹은 프로필 설정이 안 되어있을 시 로그인 시도 및 계정 생성
            if (response.result == null || response.result.ageRange == 0
                || response.result.gender == 0
            ) {
                // TODO: 로그인, 회원가입 엔드포인트 분리하여 구현 변경
                val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
                oneTapClient = Identity.getSignInClient(this@MainActivity)
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
                    .addOnSuccessListener(this@MainActivity) { result ->
                        try {
                            startIntentSenderForResult(
                                result.pendingIntent.intentSender, REQ_ONE_TAP,
                                null, 0, 0, 0, null
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e("oneTapUiFailure", "Couldn't start One Tap UI: ${e.localizedMessage}")
                        }
                    }
                    .addOnFailureListener(this@MainActivity) { e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d("beginSignInFailure", e.localizedMessage)
                    }
            } else if (response.resultCode == NetworkModule.SUCCESS) {
                // jwt가 유효하고 프로필이 설정되어 있을 시, 정상 로그인 처리
            }
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
                    val familyName = credential.familyName
                    val givenName = credential.givenName
//                    Log.d("loginResultHandler", "method operated")
                    if (idToken != null) {
//                        Log.d("loginResultHandler", "Got ID token, $idToken")
                        CoroutineScope(Dispatchers.Main).launch {
                            val jwtResponse = withContext(Dispatchers.Default) {
                                NetworkModule.getAuthService().postGoogleIdToken(
                                    SignInRequestBody(idToken)
                                )
                            }
                            App.preferenceUtil.setJwt(jwtResponse.body().toString())
                            val profileGetResponse = NetworkModule.getProfileService().getProfile()
                            // 계정 생성 시 ageRange = 0, gender = 0, nickName = "구글 계정의 이름" 이 할당됨.
                            if (profileGetResponse.result.ageRange == 0 || profileGetResponse.result.gender == 0
//                                || profileResponse.result.nickName == null
                            ) {
                                // 새 유저일 경우, 초기설정 화면으로 fragment 전환
                                signUpViewModel = ViewModelProvider(this@MainActivity)[SignUpViewModel::class.java]
                                val fullName = familyName + givenName
                                signUpViewModel.nickName = fullName
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.frm_main, SignUpFragment1())
                                    .addToBackStack("mainActivity")
                                    .commit()
                            }
                        }
                    }
                    if (password != null) {
//                        Log.d("loginResultHandler", "Got password., $password")
                    }
                    if (username != null) {
//                        Log.d("loginResultHandler", "Got username, $username")
                    }

                } catch (e: ApiException) {
                    Log.e("loginResultHandler", e.toString())
                }
            }
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

//        // 모든 권한이 허용된 경우
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
//            && GpsManager.arePermissionsGranted(this)
//            && NotificationManager.isPermissionGranted(this)
//        ) {
//            // 포그라운드 서비스 시작
//            val serviceIntent = Intent(this, GpsForegroundService::class.java)
//            ContextCompat.startForegroundService(this, serviceIntent)
//            return
//        }
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
//            && GpsManager.arePermissionsGranted(this)
//            && !NotificationManager.isPermissionGranted(this)
//        ) {
//            NotificationManager.initNotificationManager(this)
//            return
//        }
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
//            && !GpsManager.arePermissionsGranted(this)
//            && NotificationManager.isPermissionGranted(this)
//        ) {
//            // 포그라운드 서비스가 진행 중이면 종료
//            if (GpsForegroundService.isServiceRunning) {
//                val serviceIntent = Intent(this, GpsForegroundService::class.java)
//                stopService(serviceIntent)
//            }
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("위치 정보 접근 권한 허용 설정 안내").setCancelable(false)
//            builder.setMessage(
//                "이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
//                        + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
//                        + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
//                        + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
//                        + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
//                        + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다."
//            )
//            builder.setPositiveButton("설정으로 이동") { _, _ ->
//                moveAppSettings(this, requestCode)
//            }
//            builder.show()
//            return
//        }
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
//            && !GpsManager.arePermissionsGranted(this)
//            && !NotificationManager.isPermissionGranted(this)
//        ) {
//            NotificationManager.initNotificationManager(this)
//        }



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
                    builder.setTitle("위치 정보 접근 권한 허용 설정 안내").setCancelable(false)
                    builder.setMessage(
                        "이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
                                + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
                                + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
                                + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
                                + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
                                + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다."
                    )
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(this, requestCode)
                    }
                    builder.show()
                } else {
                    NotificationManager.initNotificationManager(this)
                }
            }
        }

        else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (NotificationManager.isPermissionGranted(this)) {
                // 권한이 허용된 경우
                if (GpsManager.arePermissionsGranted(this)) {
                    // 포그라운드 서비스 시작
                    val serviceIntent = Intent(this, GpsForegroundService::class.java)
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("위치 정보 접근 권한 허용 설정 안내").setCancelable(false)
                    builder.setMessage(
                        "이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
                                + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
                                + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
                                + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
                                + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
                                + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다."
                    )
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
                    builder.setTitle("위치 정보 접근 권한 허용 설정 안내").setCancelable(false)
                    builder.setMessage(
                        "이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
                                + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
                                + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
                                + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
                                + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
                                + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다."
                    )
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        moveAppSettings(this, requestCode)
                    }
                    builder.show()
                }

            }
        }
    }

//    private fun buildLocationAlert(): AlertDialog.Builder {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("위치 정보 접근 권한 허용 설정 안내").setCancelable(false)
//        builder.setMessage(
//            "이 앱은 앱이 종료되었거나 사용 중이 아닐 때도 실시간으로 위치 데이터를 수집하여 사용자의 위치를 파악하고,"
//                    + " 주변 지하철역을 탐색하여 열차의 도착 안내를 제공합니다."
//                    + " 그리고 해당 기능을 이용하기 위하여, 위치 권한의 설정이 필요합니다.\n"
//                    + "'설정 - 권한' 에서 위치 정보 접근 권한을 '항상 허용' 으로 설정해 주세요.\n\n"
//                    + " ※ 위치 정보 수집을 거부하시면 사용자님의 위치는 수집되지 않습니다. 그러나 해당 권한이 앱의 주요 기능에"
//                    + " 필수적임에 따라, 앱을 이용하실 수 없게 됩니다."
//        )
//        builder.setPositiveButton("설정으로 이동") { _, _ ->
//            moveAppSettings(this, LOCATION_PERMISSION_REQUEST_CODE)
//        }
//        return builder
//    }

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
