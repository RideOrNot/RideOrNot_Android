package com.hanium.rideornot.ui

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentLogin1Binding

private const val FIRST_ANIM_DELAY: Long = 400
private const val SECOND_ANIM_DELAY: Long = 800
private const val THIRD_ANIM_DELAY: Long = 1200

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentLogin1Binding

    enum class Age(code: Int) {
        NONE(0), TEENS(1), TWENTIES(2),
        THIRTIES(3), FORTIES(4), FIFTIES(5), ELDERS(6)
    }
    enum class Gender(code: Int) {
        NONE(0), MALE(1), FEMALE(2)
    }

    private var age: Age = Age.NONE
    private var gender: Gender = Gender.NONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        binding.clSecondLayout.visibility = View.GONE

        val fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim.startOffset = FIRST_ANIM_DELAY

        binding.tvHi.startAnimation(fadeInAnim)
        fadeInAnim.startOffset = SECOND_ANIM_DELAY
        binding.tvExplanation.startAnimation(fadeInAnim)
        fadeInAnim.startOffset = THIRD_ANIM_DELAY
        binding.btnStart.startAnimation(fadeInAnim)

        return binding.root
    }

}







