package com.hanium.rideornot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hanium.rideornot.databinding.ActivityErrorBinding
import com.hanium.rideornot.databinding.ActivityStationDetailBinding
import com.hanium.rideornot.ui.SplashActivity
import kotlin.system.exitProcess

class ErrorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("ErrorActivity", "errorActivity started")

        binding.button.setOnClickListener {
            // 루트 액티비티를 종료
            finishAffinity()
            // 현재 작업중인 쓰레드가 다 종료되면, 종료시키라는 명령어
            System.runFinalization()
            // 현재 액티비티를 종료시킨다.
            startActivity(Intent(this@ErrorActivity, SplashActivity::class.java))
            exitProcess(0)
        }
    }

    override fun onResume() {
        super.onResume()

    }
}