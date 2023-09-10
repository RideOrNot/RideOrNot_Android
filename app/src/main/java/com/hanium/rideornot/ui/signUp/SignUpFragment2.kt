package com.hanium.rideornot.ui.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSignIn2Binding
import com.hanium.rideornot.ui.signUp.SignUpViewModel.AgeRange


class SignUpFragment2 : Fragment() {
    private lateinit var binding: FragmentSignIn2Binding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentSignIn2Binding.inflate(inflater, container, false)
        signUpViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        fadeOutAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
                parentFragmentManager.beginTransaction().replace(R.id.frm_main, SignUpFragment3()).commit()
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        binding.btnTeens.setOnClickListener {
            signUpViewModel.ageRange = AgeRange.TEENS
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnTwenties.setOnClickListener {
            signUpViewModel.ageRange = AgeRange.TWENTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnThirties.setOnClickListener {
            signUpViewModel.ageRange = AgeRange.THIRTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnForties.setOnClickListener {
            signUpViewModel.ageRange = AgeRange.FORTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnFifties.setOnClickListener {
            signUpViewModel.ageRange = AgeRange.FIFTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnElders.setOnClickListener {
            signUpViewModel.ageRange = AgeRange.ELDERS
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }

        return binding.root
    }


    private fun disableButtons() {
        binding.btnTeens.isClickable = false
        binding.btnTwenties.isClickable = false
        binding.btnThirties.isClickable = false
        binding.btnForties.isClickable = false
        binding.btnFifties.isClickable = false
        binding.btnElders.isClickable = false
    }
}