package com.hanium.rideornot.ui.signUp

import android.content.Context
import androidx.lifecycle.ViewModel
import com.hanium.rideornot.data.response.ProfileGetResponse


class SignUpViewModel : ViewModel() {
    enum class AgeRange(val id: Int, val text: String) {
        NONE(0, "식별 불가"), TEENS(10, "10대"), TWENTIES(20, "20대"),
        THIRTIES(30, "30대"), FORTIES(40, "40대"), FIFTIES(50, "50대"),
        ELDERS(60, "60대 이상")
    }

    enum class Gender(val id: Int, val text: String) {
        NONE(0, "식별 불가"), MALE(1, "남성"), FEMALE(2, "여성")
    }

    var ageRange: AgeRange = AgeRange.NONE
    var gender: Gender = Gender.NONE
    var nickName: String = ""
}