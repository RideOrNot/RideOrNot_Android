package com.hanium.rideornot.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSettingBinding
import com.hanium.rideornot.ui.dialog.VerticalDialog
import com.hanium.rideornot.ui.signUp.SignUpViewModel

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val settingViewModel: SettingViewModel by activityViewModels()
    private lateinit var signUpViewModel: SignUpViewModel

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
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        signUpViewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java]
        setBackBtnHandling()

        initView()

        binding.tvNickname.text = signUpViewModel.profiles.nickName
        binding.tvEmail.text = signUpViewModel.profiles.email

        return binding.root
    }

    private fun initView() {
        binding.btnLogout.setOnClickListener {
            val dialog = VerticalDialog(requireContext() as AppCompatActivity)
            dialog.show(
                getString(R.string.logout_message),
                null,
                getString(R.string.logout),
                getString(R.string.cancel)
            )
        }

        binding.btnUnregister.setOnClickListener {
            val dialog = VerticalDialog(requireContext() as AppCompatActivity)
            dialog.show(
                getString(R.string.unregister_message),
                getString(R.string.unregister_caption),
                getString(R.string.unregister),
                getString(R.string.cancel)
            )
        }
    }

}