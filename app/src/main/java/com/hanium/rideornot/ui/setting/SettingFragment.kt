package com.hanium.rideornot.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hanium.rideornot.App
import com.hanium.rideornot.MainActivity
import com.hanium.rideornot.R
import com.hanium.rideornot.data.response.ProfileGetResponse
import com.hanium.rideornot.databinding.FragmentSettingBinding
import com.hanium.rideornot.ui.dialog.VerticalDialog
import com.hanium.rideornot.utils.NetworkModule
import kotlinx.coroutines.*
import retrofit2.Response

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var profiles: ProfileGetResponse

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

        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.Default) {
                NetworkModule.getProfileService().getProfile()
            }
            profiles = response.result
            Log.d("responseSetting", response.toString())
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
                "기기에 두 개 이상의 구글 계정이 등록되어\n있을 경우, 계정을 변경할 수 있습니다.",
                getString(R.string.logout),
                getString(R.string.cancel)
            )
            dialog.topBtnClickListener {
                if (it) {
                    App.signOut()
                    App.startSignIn(requireActivity() as MainActivity)
                    true
                }
            }
            dialog.bottomBtnClickListener { it ->
                if (it) true
            }
        }

        binding.btnUnregister.setOnClickListener {
            val dialog = VerticalDialog(requireContext() as AppCompatActivity)
            dialog.show(
                getString(R.string.unregister_message),
                getString(R.string.unregister_caption),
                getString(R.string.unregister),
                getString(R.string.cancel)
            )
            dialog.topBtnClickListener { dialogTopBtnClicked ->
                if (dialogTopBtnClicked) {
                    val confirmationDialog = VerticalDialog(requireContext() as AppCompatActivity)
                    confirmationDialog.show(
                        getString(R.string.unregister_confirmation_message),
                        getString(R.string.unregister_confirmation_caption),
                        getString(R.string.unregister_confirmation_top_btn_title),
                        getString(R.string.cancel)
                    )
                    confirmationDialog.topBtnClickListener {
                        if (it) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (profiles == null) {
                                    Toast.makeText(requireContext(), getString(R.string.action_fail), Toast.LENGTH_SHORT)
                                    return@launch
                                }
                                // TODO: 서버 응답 BaseResponse 형식으로 바뀌면 구현 수정하기
                                lateinit var deleteResponse: Response<String>
                                withContext(Dispatchers.Default) {
                                   deleteResponse = NetworkModule.getProfileService().deleteUser(profiles.id)
                                }
                                if (deleteResponse.isSuccessful) {
                                    App.signOut()
                                    App.startSignIn(requireActivity() as MainActivity)
                                }
                            }
                            true
                        }
                    }
                    true

                    confirmationDialog.bottomBtnClickListener {
                        if (it) true
                    }
                }
            }
            dialog.bottomBtnClickListener { dialogBottomBtnClicked ->
                if (dialogBottomBtnClicked) true
            }
        }
    }

}