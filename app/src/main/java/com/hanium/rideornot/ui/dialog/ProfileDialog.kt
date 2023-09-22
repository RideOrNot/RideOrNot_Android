package com.hanium.rideornot.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.hanium.rideornot.databinding.DialogProfileBinding
import com.hanium.rideornot.ui.signUp.SignUpViewModel

class ProfileDialog(private val context: AppCompatActivity) {

    private lateinit var binding: DialogProfileBinding
    private val dialog = Dialog(context)
    private lateinit var listener: CheckBtnClickedListener

    fun show(email: String, nickName: String, gender: Int, ageRange: Int) {
        binding = DialogProfileBinding.inflate(context.layoutInflater)
        dialog.setContentView(binding.root)

        // 배경 색 투명
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 창 크기
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        binding.tvProfileEmail.text = email
        binding.tvProfileNickname.text = nickName
        binding.tvProfileGender.text = when (gender) {
            1 -> SignUpViewModel.Gender.MALE.text
            2 -> SignUpViewModel.Gender.FEMALE.text
            else -> SignUpViewModel.Gender.NONE.text
        }
        binding.tvProfileAgeRange.text = when (ageRange) {
            10 -> SignUpViewModel.AgeRange.TEENS.text
            20 -> SignUpViewModel.AgeRange.TWENTIES.text
            30 -> SignUpViewModel.AgeRange.THIRTIES.text
            40 -> SignUpViewModel.AgeRange.FORTIES.text
            50 -> SignUpViewModel.AgeRange.FIFTIES.text
            60 -> SignUpViewModel.AgeRange.ELDERS.text
            else -> SignUpViewModel.Gender.NONE.text
        }


        binding.tvProfileResetBtn.setOnClickListener {
            // TODO: 프로필 재설정 버튼 구현
        }

        binding.btnCheck.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    fun btnClickListener(listener: (Boolean) -> Unit) {
        this.listener = object : CheckBtnClickedListener {
            override fun onClicked(content: Boolean) {
                listener(content)
            }
        }
    }

    interface CheckBtnClickedListener {
        fun onClicked(content: Boolean)
    }
}