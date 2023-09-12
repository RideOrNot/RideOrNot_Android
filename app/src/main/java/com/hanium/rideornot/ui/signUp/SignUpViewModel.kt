package com.hanium.rideornot.ui.signUp

import android.content.Context
import androidx.lifecycle.ViewModel
import com.hanium.rideornot.data.response.ProfileGetResponse


class SignUpViewModel: ViewModel() {
    enum class AgeRange (val id: Int) {
        NONE(0), TEENS(10), TWENTIES(20),
        THIRTIES(30), FORTIES(40), FIFTIES(50), ELDERS(60)
    }

    enum class Gender (val id: Int) {
        NONE(0), MALE(1), FEMALE(2)
    }

    lateinit var profiles: ProfileGetResponse
}