package com.hanium.rideornot.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSettingBinding
import com.hanium.rideornot.ui.dialog.VerticalDialog
import com.hanium.rideornot.utils.NetworkModule
import kotlinx.coroutines.*

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val settingViewModel: SettingViewModel by activityViewModels()
//    private lateinit var signUpViewModel: SignUpViewModel

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

        setBackBtnHandling()

        initView()

//        signUpViewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java]
        CoroutineScope(Dispatchers.Main).launch {
                val response = withContext(Dispatchers.Default) {
                NetworkModule.getProfileService().getProfile()
            }
            Log.d("responseSetting",response.toString())
            binding.tvNickname.text = response.result.nickName
            binding.tvEmail.text = response.result.email
        }

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