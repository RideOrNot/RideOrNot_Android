package com.hanium.rideornot.ui.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSignIn1Binding

private const val FIRST_ANIM_DELAY: Long = 400
private const val SECOND_ANIM_DELAY: Long = 1200
private const val THIRD_ANIM_DELAY: Long = 2000

class SignInFragment1 : Fragment() {
    private lateinit var binding: FragmentSignIn1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentSignIn1Binding.inflate(inflater, container, false)

        val fadeInAnim1 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim1.startOffset = FIRST_ANIM_DELAY
        val fadeInAnim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim2.startOffset = SECOND_ANIM_DELAY
        val fadeInAnim3 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim3.startOffset = THIRD_ANIM_DELAY

        binding.tvHi.startAnimation(fadeInAnim1)
        binding.tvExplanation.startAnimation(fadeInAnim2)
        binding.btnStart.startAnimation(fadeInAnim3)

        binding.btnStart.setOnClickListener {
            parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.frm_main, SignInFragment2()).commit()
        }

        return binding.root
    }

}