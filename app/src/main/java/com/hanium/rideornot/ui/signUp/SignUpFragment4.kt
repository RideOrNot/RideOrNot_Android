package com.hanium.rideornot.ui.signUp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSignIn4Binding

private const val FIRST_ANIM_DELAY: Long = 0
private const val SECOND_ANIM_DELAY: Long = 800
private const val MAX_NICKNAME_BYTES: Int = 24

class SignUpFragment4 : Fragment() {
    private lateinit var binding: FragmentSignIn4Binding
    private lateinit var signUpViewModel: SignUpViewModel

    private var currentBytes = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentSignIn4Binding.inflate(inflater, container, false)
        signUpViewModel = SignUpViewModel(requireContext())

        val fadeInAnim1 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim1.startOffset = FIRST_ANIM_DELAY
        binding.clNicknameInput.startAnimation(fadeInAnim1)
        val fadeInAnim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnim2.startOffset = SECOND_ANIM_DELAY
        binding.tvOkBtn.startAnimation(fadeInAnim2)

        val fillFromLeftAnim = AnimationUtils.loadAnimation(context, R.anim.fill_from_left)
        binding.ivProgressBarGaugeRight.startAnimation(fillFromLeftAnim)

        val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        fadeOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                parentFragmentManager.beginTransaction().replace(R.id.frm_main, SignUpFragment4()).commit()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        binding.tvNicknameCurrentBytes.text = currentBytes.toString()
        binding.tvNicknameMaxBytes.text = MAX_NICKNAME_BYTES.toString()
        binding.editTextNicknameInput.hint = signUpViewModel.name

        binding.editTextNicknameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val textBytes = text.toByteArray(Charsets.UTF_8)
                currentBytes = textBytes.size
                binding.tvNicknameCurrentBytes.text = currentBytes.toString()
                if (currentBytes > MAX_NICKNAME_BYTES) {
                    binding.tvNicknameCurrentBytes.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                } else {
                    binding.tvNicknameCurrentBytes.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_700))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        binding.tvOkBtn.setOnClickListener {
            binding.tvOkBtn.isClickable = false
            if (binding.editTextNicknameInput.text.toString() == "") {
                if (signUpViewModel.name != "") {
                    TODO("닉네임 = name")
                }
            }
            disableButtons()
        }

        return binding.root
    }

    private fun disableButtons() {
        binding.tvOkBtn.isClickable = false
    }

}