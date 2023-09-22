package com.hanium.rideornot.ui.setting

import android.content.Context
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
import com.hanium.rideornot.ui.dialog.ProfileDialog
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
    private lateinit var context: Context

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
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
            if (response.result != null) {
                binding.tvNickname.text = response.result.nickName
                binding.tvEmail.text = response.result.email
                Toast.makeText(context, R.string.toast_login_success, Toast.LENGTH_SHORT)
            } else {
                binding.tvNickname.text = "계정 정보 확인 불가"
            }
        }
    }

    override fun onLoginFailure() {
        Toast.makeText(context, R.string.toast_login_failure, Toast.LENGTH_SHORT)
    }

    private fun initView() {
        binding.btnLogout.setOnClickListener {
            val dialog = VerticalDialog(context as AppCompatActivity)
            dialog.show(
                getString(R.string.logout_message),
                getString(R.string.logout_caption),
                getString(R.string.logout),
                getString(R.string.cancel)
            )
            dialog.topBtnClickListener {
                if (it) {
                    if (profiles != null) {
                        Toast.makeText(context, getString(R.string.toast_logout_success), Toast.LENGTH_SHORT).show()
                        App.signOut()
                        App.startSignIn(requireActivity() as MainActivity)
                    } else {
                        Toast.makeText(context, getString(R.string.toast_account_not_exists), Toast.LENGTH_SHORT).show()
                        App.signOut()
                    }
                    true
                }
            }
            dialog.bottomBtnClickListener { it ->
                if (it) true
            }
        }

        binding.btnUnregister.setOnClickListener {
            val dialog = VerticalDialog(context as AppCompatActivity)
            dialog.show(
                getString(R.string.unregister_message),
                getString(R.string.unregister_caption),
                getString(R.string.unregister),
                getString(R.string.cancel)
            )
            dialog.topBtnClickListener { dialogTopBtnClicked ->
                if (dialogTopBtnClicked) {
                    val confirmationDialog = VerticalDialog(context as AppCompatActivity)
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
                                    Toast.makeText(context, getString(R.string.action_fail), Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                // TODO: 서버 응답 BaseResponse 형식으로 바뀌면 구현 수정하기
                                lateinit var deleteResponse: Response<String>
                                withContext(Dispatchers.Default) {
                                    deleteResponse = NetworkModule.getProfileService().deleteUser(profiles!!.id)
                                }
                                if (deleteResponse.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.toast_unregister_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
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

        binding.btnAccountInfo.setOnClickListener {
            if (profiles == null) {
                Toast.makeText(context, getString(R.string.toast_account_not_exists), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ProfileDialog(context as AppCompatActivity).show(
                profiles!!.email, profiles!!.nickName, profiles!!.gender, profiles!!.ageRange
            )
        }

        binding.btnWalkingSpeedSetting.setOnClickListener {
            // TODO: 걸음 속도 설정 구현하기
            BaseDialog(context as AppCompatActivity).show(getString(R.string.toast_not_yet_implemented))
        }

        binding.switchPushNotificationReception.isChecked =
            App.prefUtil.prefs.getBoolean(PreferenceUtil.PUSH_NOTIFICATION_KEY, true)
        binding.switchPushNotificationReception.setOnClickListener {
            if (binding.switchPushNotificationReception.isChecked) {
                App.prefUtil.prefs.edit().putBoolean(PreferenceUtil.PUSH_NOTIFICATION_KEY, true).apply()
                Toast.makeText(context, getString(R.string.toast_notification_on), Toast.LENGTH_SHORT).show()
            } else {
                App.prefUtil.prefs.edit().putBoolean(PreferenceUtil.PUSH_NOTIFICATION_KEY, false).apply()
                Toast.makeText(context, getString(R.string.toast_notification_off), Toast.LENGTH_SHORT).show()
            }
        }
    }
}