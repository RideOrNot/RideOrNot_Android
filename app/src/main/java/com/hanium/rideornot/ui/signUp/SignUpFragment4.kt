package com.hanium.rideornot.ui.signUp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.R
import com.hanium.rideornot.data.response.ProfileDto
import com.hanium.rideornot.databinding.FragmentSignIn4Binding
import com.hanium.rideornot.ui.home.HomeFragment
import com.hanium.rideornot.utils.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        signUpViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

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

            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        binding.tvNicknameCurrentBytes.text = signUpViewModel.nickName.toByteArray(Charsets.UTF_8).size.toString()
        binding.tvNicknameMaxBytes.text = MAX_NICKNAME_BYTES.toString()
        binding.editTextNicknameInput.hint = signUpViewModel.nickName

        binding.editTextNicknameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()

                currentBytes = if (text == "") signUpViewModel.nickName.toByteArray(Charsets.UTF_8).size
                else text.toByteArray(Charsets.UTF_8).size

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

        // TODO: 글자수 제한 넘으면 재설정 플로팅 출력
        binding.tvOkBtn.setOnClickListener {
            Log.d("okBtn", "clicked")
            val profileDto: ProfileDto = if (binding.editTextNicknameInput.text.toString() == "") {
                ProfileDto(
                    ageRange = signUpViewModel.ageRange.id,
                    gender = signUpViewModel.gender.id,
                    nickName = signUpViewModel.nickName
                )
            } else {
                ProfileDto(
                    ageRange = signUpViewModel.ageRange.id,
                    gender = signUpViewModel.gender.id,
                    nickName = binding.editTextNicknameInput.text.toString()
                )
            }
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("response-prepre", "testtesttest")
                val response = withContext(Dispatchers.Default) {
                    NetworkModule.getProfileService().postProfile(profileDto)
                }
                Log.d("response-pre", "testtesttest")
                Log.d("response", response.toString())
                if (response.isSuccessful) {
//                    parentFragmentManager.beginTransaction().remove(this@SignUpFragment4).commit()
                    parentFragmentManager.popBackStack()
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