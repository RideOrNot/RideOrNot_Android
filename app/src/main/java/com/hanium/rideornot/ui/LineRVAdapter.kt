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
            val selectedPosition = holder.adapterPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                val previousSelectedPosition = selectedItemPosition
                selectedItemPosition = selectedPosition

                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
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

    // 호선별 커스텀
    private fun setLineCustom(
        item: Line,
        binding: ItemLineBinding,
        context: Context,
        isSelected: Boolean
    ) {
        when (item.lineName) {
            "1호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_1))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_1)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "2호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_2))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_2)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "3호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_3))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_3)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "4호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_4))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_4)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "5호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_5))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_5)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "6호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_6))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_6)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "7호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_7))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_7)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "8호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_8))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_8)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "9호선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line_9))
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_9)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.csvLine.toString()
            }
            "분당선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.line_bundang
                            )
                        )
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_bundang)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.lineName
            }
            "신분당선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.line_sinbundang
                            )
                        )
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_sinbundang)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.lineName
            }
            "우이신설선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.line_ui_sinseol
                            )
                        )
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_ui_sinseol)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.lineName
            }
            "경의중앙선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.line_gyeongui_jungang
                            )
                        )
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_gyeongui_jungang)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.lineName
            }
            "공항철도" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.line_airport_rail_link
                            )
                        )
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_airport_rail_link)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.lineName
            }
            "경춘선" -> {
                if (isSelected) {
                    binding.btnLine.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.line_gyeongchun
                            )
                        )
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    binding.btnLine.setTextColor(ContextCompat.getColor(context, R.color.black))

                    // 버튼의 배경 Drawable 테두리(border) 색상 변경
                    val backgroundDrawable = binding.btnLine.background as? GradientDrawable
                    backgroundDrawable?.setStroke(
                        4,
                        ContextCompat.getColor(context, R.color.line_gyeongchun)
                    )
                    binding.btnLine.background = backgroundDrawable
                }
                binding.btnLine.text = item.lineName
            }
        }
    }

}