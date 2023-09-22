package com.hanium.rideornot

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.hanium.rideornot.domain.StationDatabase
import com.hanium.rideornot.repository.LineRepository
import com.hanium.rideornot.repository.StationExitRepository
import com.hanium.rideornot.repository.StationRepository
import com.hanium.rideornot.utils.PreferenceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.system.exitProcess

class App : Application() {
    companion object {
        val applicationScope = CoroutineScope(SupervisorJob())

        private val database by lazy { StationDatabase.getInstance(getApplicationContext()) }

        val lineRepository by lazy { LineRepository(database!!.lineDao()) }
        val stationRepository by lazy { StationRepository(database!!.stationDao()) }
        val stationExitRepository by lazy { StationExitRepository(database!!.stationExitDao()) }

        lateinit var instance: App

        lateinit var prefUtil: PreferenceUtil

        fun getApplicationContext(): Context {
            return instance.applicationContext
        }

        fun signOut(mainActivity: MainActivity) {
            mainActivity.signOutOneTapClient()
            prefUtil.setJwt("")
        }

        fun startSignIn(mainActivity: MainActivity) {
            mainActivity.startSignIn()
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefUtil = PreferenceUtil(applicationContext)
        instance = this
        // 다크모드 임시 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            // 예외 처리 코드
//            throwable.printStackTrace()
            // 또는 오류 메시지를 사용자에게 표시
            // 예외 처리 후 앱 종료 여부를 결정할 수 있음
            Log.d("exception handle","handling")
            caughtException()
        }
    }

    private fun caughtException(){
        // start error activity
        startErrorActivity()
        // kill process
//        Process.killProcess(Process.myPid())
        Log.d("startErrorActivity", "initiated")
//        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(-1)
    }

    private fun startErrorActivity(){
        val intent = Intent(this, ErrorActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}