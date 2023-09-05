package com.hanium.rideornot.ui.signUp

import android.content.Context
import androidx.lifecycle.ViewModel


class SignUpViewModel(context: Context): ViewModel() {
    enum class Age {
        NONE, TEENS, TWENTIES,
        THIRTIES, FORTIES, FIFTIES, ELDERS
    }

    enum class Gender {
        NONE, MALE, FEMALE
    }

    var age: Age = Age.NONE
    var gender: Gender = Gender.NONE
    var name: String = ""

}