package com.hanium.rideornot.ui.setting

import androidx.lifecycle.ViewModel
import com.hanium.rideornot.data.response.ProfileGetResponse

class SettingViewModel : ViewModel() {
    lateinit var profiles: ProfileGetResponse
}