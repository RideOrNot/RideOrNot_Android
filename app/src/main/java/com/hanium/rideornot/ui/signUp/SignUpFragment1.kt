package com.hanium.rideornot.ui.signUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSignUp1Binding

private const val FIRST_ANIM_DELAY: Long = 400
private const val SECOND_ANIM_DELAY: Long = 1200
private const val THIRD_ANIM_DELAY: Long = 2000

class SignUpFragment1 : Fragment() {
    private lateinit var binding: FragmentSignUp1Binding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private fun setBackBtnHandling() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        signUpViewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java]
        binding = FragmentSignUp1Binding.inflate(inflater, container, false)
        setBackBtnHandling()

        val fadeInAnim1 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim1.startOffset = FIRST_ANIM_DELAY
        val fadeInAnim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim2.startOffset = SECOND_ANIM_DELAY
        val fadeInAnim3 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim3.startOffset = THIRD_ANIM_DELAY
        Log.d("FragmentContext", requireContext().toString())
        Log.d("FullName2", signUpViewModel.nickName)

        binding.tvHi.startAnimation(fadeInAnim1)
        binding.tvExplanation.startAnimation(fadeInAnim2)
        binding.btnStart.startAnimation(fadeInAnim3)

        binding.btnStart.setOnClickListener {
            parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.frm_main, SignUpFragment2()).commit()
            disableButtons()
        }

        return binding.root
    }

    private fun disableButtons() {
        binding.btnStart.isClickable = false
    }
}