package com.hanium.rideornot.ui.signUp

import androidx.fragment.app.Fragment

class SignUpFragment : Fragment() {
    enum class AgeRange (val id: Int) {
        NONE(0), TEENS(10), TWENTIES(20),
        THIRTIES(30), FORTIES(40), FIFTIES(50), ELDERS(60)
    }

    enum class Gender (val id: Int) {
        NONE(0), MALE(1), FEMALE(2)
    }



}