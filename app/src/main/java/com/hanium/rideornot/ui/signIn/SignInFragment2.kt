package com.hanium.rideornot.ui.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSignIn2Binding
import com.hanium.rideornot.ui.signIn.SignInViewModel.Age


class SignInFragment2 : Fragment() {
    private lateinit var binding: FragmentSignIn2Binding
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentSignIn2Binding.inflate(inflater, container, false)
        signInViewModel = SignInViewModel(requireContext())

        val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        fadeOutAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
                parentFragmentManager.beginTransaction().replace(R.id.frm_main, SignInFragment3()).commit()
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        binding.btnTeens.setOnClickListener {
            signInViewModel.age = Age.TEENS
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnTwenties.setOnClickListener {
            signInViewModel.age = Age.TWENTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnThirties.setOnClickListener {
            signInViewModel.age = Age.THIRTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnForties.setOnClickListener {
            signInViewModel.age = Age.FORTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnFifties.setOnClickListener {
            signInViewModel.age = Age.FIFTIES
            binding.llAgeSelection.startAnimation(fadeOutAnim)
            disableButtons()
        }
        binding.btnElders.setOnClickListener {
            signInViewModel.age = Age.ELDERS
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