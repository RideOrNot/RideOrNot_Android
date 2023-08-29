package com.hanium.rideornot.ui.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSignIn4Binding


class SignInFragment4 : Fragment() {
    private lateinit var binding: FragmentSignIn4Binding
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentSignIn4Binding.inflate(inflater, container, false)
        signInViewModel = SignInViewModel(requireContext())

        val fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        binding.llGenderSelection.startAnimation(fadeInAnim)

        val fillFromLeftAnim = AnimationUtils.loadAnimation(context, R.anim.fill_from_left)
        binding.ivProgressBarGaugeRight.startAnimation(fillFromLeftAnim)

        val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        fadeOutAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
                parentFragmentManager.beginTransaction().replace(R.id.frm_main, SignInFragment4()).commit()
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        return binding.root
    }

}