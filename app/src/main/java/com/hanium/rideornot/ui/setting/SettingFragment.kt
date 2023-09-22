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
import com.hanium.rideornot.*
import com.hanium.rideornot.data.response.ProfileGetResponse
import com.hanium.rideornot.databinding.FragmentSettingBinding
import com.hanium.rideornot.ui.dialog.BaseDialog
import com.hanium.rideornot.ui.dialog.VerticalDialog
import com.hanium.rideornot.utils.NetworkModule
import com.hanium.rideornot.utils.PreferenceUtil
import com.hanium.rideornot.utils.observers.ILoginResultListener
import kotlinx.coroutines.*
import retrofit2.Response

class SettingFragment : Fragment(), ILoginResultListener {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var profiles: ProfileGetResponse? = null

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

        MainActivity.loginResultObserver.addListener(this)

        setBackBtnHandling()
        initView()

        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.Default) {
                NetworkModule.getProfileService().getProfile()
            }
            profiles = response.result
            Log.d("responseSetting", response.toString())
            if (response.result != null) {
                binding.tvNickname.text = response.result.nickName
                binding.tvEmail.text = response.result.email
            } else {
                binding.tvNickname.text = "계정 정보 확인 불가"
            }
        }

        return binding.root
    }

    override fun onLoginSuccess() {
        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.Default) {
                NetworkModule.getProfileService().getProfile()
            }
            profiles = response.result
            Log.d("responseSetting", response.toString())
            if (response.result != null) {
                binding.tvNickname.text = response.result.nickName
                binding.tvEmail.text = response.result.email
                Toast.makeText(requireContext(),R.string.toast_login_success, Toast.LENGTH_SHORT)
            } else {
                binding.tvNickname.text = "계정 정보 확인 불가"
            }
        }
    }

    override fun onLoginFailure() {
        Toast.makeText(requireContext(),R.string.toast_login_failure, Toast.LENGTH_SHORT)
    }

    private fun initView() {
        binding.btnLogout.setOnClickListener {
            val dialog = VerticalDialog(requireContext() as AppCompatActivity)
            dialog.show(
                getString(R.string.logout_message),
                getString(R.string.logout_caption),
                getString(R.string.logout),
                getString(R.string.cancel)
            )
            dialog.topBtnClickListener {
                if (it) {
                    if (profiles != null) {
                        Toast.makeText(requireContext(), getString(R.string.toast_logout_success), Toast.LENGTH_SHORT).show()
                        App.signOut(requireActivity() as MainActivity)
                        App.startSignIn(requireActivity() as MainActivity)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.toast_logout_failure), Toast.LENGTH_SHORT).show()
                        App.signOut(requireActivity() as MainActivity)
                    }
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
                                    Toast.makeText(requireContext(), getString(R.string.action_fail), Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                // TODO: 서버 응답 BaseResponse 형식으로 바뀌면 구현 수정하기
                                lateinit var deleteResponse: Response<String>
                                withContext(Dispatchers.Default) {
                                    deleteResponse = NetworkModule.getProfileService().deleteUser(profiles!!.id)
                                }
                                if (deleteResponse.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.toast_unregister_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    App.signOut(requireActivity() as MainActivity)
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

        binding.btnAccountInfo.setOnClickListener {
            // TODO: 계정 정보 설정 구현하기
            BaseDialog(requireContext() as AppCompatActivity).show(getString(R.string.toast_not_yet_implemented))
        }

        binding.btnWalkingSpeedSetting.setOnClickListener {
            // TODO: 걸음 속도 설정 구현하기
            BaseDialog(requireContext() as AppCompatActivity).show(getString(R.string.toast_not_yet_implemented))
        }

        binding.switchPushNotificationReception.setOnClickListener {
            App.preferenceUtil.prefs.edit().putBoolean(PreferenceUtil.PUSH_NOTIFICATION_KEY, true)
        }
    }
}