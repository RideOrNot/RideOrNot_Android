package com.hanium.rideornot.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.ItemLineBinding
import com.hanium.rideornot.domain.Line
import com.hanium.rideornot.utils.methods.getLineColorIdByLineName

class LineRVAdapter(var lineList: ArrayList<Line>) :
    RecyclerView.Adapter<LineRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(line: Line)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    var selectedItemPosition = 0

    fun updateData(newLineList: ArrayList<Line>) {
        lineList = newLineList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemLineBinding =
            ItemLineBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )

        return ViewHolder(binding, viewGroup.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lineList[position], position == selectedItemPosition)
        holder.binding.btnLine.setOnClickListener {
            if (selectedItemPosition != position) {
                val previousSelectedPosition = selectedItemPosition
                selectedItemPosition = position

                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedItemPosition)
            }
            mItemClickListener.onItemClick(lineList[position])
        }
    }

    override fun getItemCount(): Int {
        return lineList.size
    }

    fun getItem(position: Int): Line {
        return lineList[position]
    }

    inner class ViewHolder(val binding: ItemLineBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        private var isSelected: Boolean = false

        fun bind(item: Line, isSelected: Boolean) {
            this.isSelected = isSelected
            setLineCustom(item, binding, context, isSelected)
        }
    }

    // 호선명 추출
    private fun getLineDisplayName(lineName: String): String {
        val suffix = "호선"
        return if (lineName.endsWith(suffix)) {
            lineName.substringBefore(suffix)
        } else {
            lineName.substringBefore("선")
        }
    }

    // 호선별 커스텀
    private fun setLineCustom(
        item: Line,
        binding: ItemLineBinding,
        context: Context,
        isSelected: Boolean
    ) {
        val lineColorResId = getLineColorIdByLineName(item.lineName)

        binding.btnLine.text = getLineDisplayName(item.lineName)
        binding.btnLine.setTextColor(
            ContextCompat.getColor(
                context,
                if (isSelected) R.color.white else R.color.black
            )
        )

        if (isSelected) {
            binding.btnLine.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, lineColorResId))
        } else {
            binding.btnLine.backgroundTintList = null
            val backgroundDrawable = binding.btnLine.background as? GradientDrawable
            backgroundDrawable?.setStroke(4, ContextCompat.getColor(context, lineColorResId))
            binding.btnLine.background = backgroundDrawable
        }
    }

}