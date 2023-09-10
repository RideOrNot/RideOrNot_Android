package com.hanium.rideornot.ui.signUp

import android.content.Context
import androidx.lifecycle.ViewModel


class SignUpViewModel(context: Context): ViewModel() {
    enum class Age (id: Int) {
        NONE(0), TEENS(10), TWENTIES(20),
        THIRTIES(30), FORTIES(40), FIFTIES(50), ELDERS(60)
    }

    enum class Gender (id: Int) {
        NONE(0), MALE(1), FEMALE(2)
    }

    var age: Age = Age.NONE
    var gender: Gender = Gender.NONE
    var name: String = ""

}