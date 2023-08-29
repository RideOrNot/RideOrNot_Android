package com.hanium.rideornot.ui.signIn

import android.content.Context
import androidx.lifecycle.ViewModel


class SignInViewModel(context: Context): ViewModel() {
    enum class Age(code: Int) {
        NONE(0), TEENS(1), TWENTIES(2),
        THIRTIES(3), FORTIES(4), FIFTIES(5), ELDERS(6)
    }

    enum class Gender(code: Int) {
        NONE(0), MALE(1), FEMALE(2)
    }

    var age: Age = Age.NONE
    var gender: Gender = Gender.NONE



}