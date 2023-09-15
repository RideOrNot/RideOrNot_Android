package com.hanium.rideornot.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.hanium.rideornot.databinding.DialogOptionBinding

class OptionDialog(private val context: AppCompatActivity) {

    private lateinit var binding: DialogOptionBinding
    private val dialog = Dialog(context)
    private lateinit var listener: CheckBtnClickedListener

    fun show(text: String) {
        binding = DialogOptionBinding.inflate(context.layoutInflater)
        dialog.setContentView(binding.root)

        // 배경 색 투명
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 창 크기
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        var userConfirmed = false  // 사용자 클릭 여부

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        binding.tvContent.text = text

        dialog.show()


        binding.btnCheck.setOnClickListener {
            userConfirmed = true
            listener.onClicked(true)
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            userConfirmed = false
            listener.onClicked(false)
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            if (!userConfirmed) {
                listener.onClicked(false)
            }
        }
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