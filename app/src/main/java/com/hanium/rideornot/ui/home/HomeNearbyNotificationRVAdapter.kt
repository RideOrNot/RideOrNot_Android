package com.hanium.rideornot.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.data.response.Arrival
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.databinding.ItemNearbyNotificationBinding

class HomeNearbyNotificationRVAdapter(
    context: Context,
    private var arrivalList: List<List<Arrival>>
) :
    RecyclerView.Adapter<HomeNearbyNotificationRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(arrival: ArrivalResponse)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun updateData(newArrivalList: List<List<Arrival>>) {
        arrivalList = newArrivalList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemNearbyNotificationBinding =
            ItemNearbyNotificationBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )

        return ViewHolder(binding, viewGroup.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrivalList[position])
    }

    override fun getItemCount(): Int {
        return arrivalList.size
    }

    inner class ViewHolder(val binding: ItemNearbyNotificationBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: List<Arrival>) {
            setLineCustom(item[0].lineId, binding, context)

            binding.btnLineNumber.text = item[0].lineId
            binding.btnLineNumber.isSelected = true

            // 상행과 하행 / 외선과 내선 방향으로 도착 정보 데이터를 나누기
            val upDirectionList = item.filter { it.direction == "상행" || it.direction == "외선" }
            val downDirectionList = item.filter { it.direction == "하행" || it.direction == "내선" }

            // 각 방향별로 가장 빠른 2개의 도착 정보 선택 (arrivalTime이 0인 경우는 제외)
            val fastestUpDirection = upDirectionList.filter { it.arrivalTime != 0 }
                .sortedBy { it.arrivalTime }
                .take(2)

            val fastestDownDirection = downDirectionList.filter { it.arrivalTime != 0 }
                .sortedBy { it.arrivalTime }
                .take(2)

            if (fastestUpDirection.isNotEmpty()) {
                binding.tvUpNoArrivalDataMessage.visibility = View.INVISIBLE
                binding.tvUpFirstArrivalStation.visibility = View.VISIBLE
                binding.tvUpFirstArrivalTime.visibility = View.VISIBLE

                val firstArrival = upDirectionList[0]
                binding.tvUpFirstArrivalStation.text = firstArrival.destination.substringBefore("행")
                binding.tvUpFirstArrivalTime.text =
                    if (firstArrival.arrivalTime / 60 == 0) "곧 도착" else "${firstArrival.arrivalTime / 60}분"

                if (upDirectionList.size >= 2) {
                    binding.tvUpSecondArrivalStation.visibility = View.VISIBLE
                    binding.tvUpSecondArrivalTime.visibility = View.VISIBLE

                    val secondArrival = upDirectionList[1]
                    binding.tvUpSecondArrivalStation.text =
                        secondArrival.destination.substringBefore("행")
                    binding.tvUpSecondArrivalTime.text =
                        if (secondArrival.arrivalTime / 60 == 0) "곧 도착" else "${secondArrival.arrivalTime / 60}분"
                }
            } else {
                binding.tvUpNoArrivalDataMessage.visibility = View.VISIBLE
                binding.tvUpNoArrivalDataMessage.isSelected = true

                binding.tvUpFirstArrivalStation.visibility = View.INVISIBLE
                binding.tvUpFirstArrivalTime.visibility = View.INVISIBLE

                binding.tvUpSecondArrivalStation.visibility = View.INVISIBLE
                binding.tvUpSecondArrivalTime.visibility = View.INVISIBLE
            }

            if (fastestDownDirection.isNotEmpty()) {
                binding.tvDownNoArrivalDataMessage.visibility = View.INVISIBLE
                binding.tvDownFirstArrivalStation.visibility = View.VISIBLE
                binding.tvDownFirstArrivalTime.visibility = View.VISIBLE

                val firstArrival = downDirectionList[0]
                binding.tvDownFirstArrivalStation.text =
                    firstArrival.destination.substringBefore("행")
                binding.tvDownFirstArrivalTime.text =
                    if (firstArrival.arrivalTime / 60 == 0) "곧 도착" else "${firstArrival.arrivalTime / 60}분"

                if (downDirectionList.size >= 2) {
                    binding.tvDownSecondArrivalStation.visibility = View.VISIBLE
                    binding.tvDownSecondArrivalTime.visibility = View.VISIBLE

                    val secondArrival = downDirectionList[1]
                    binding.tvDownSecondArrivalStation.text =
                        secondArrival.destination.substringBefore("행")
                    binding.tvDownSecondArrivalTime.text =
                        if (secondArrival.arrivalTime / 60 == 0) "곧 도착" else "${secondArrival.arrivalTime / 60}분"
                }
            } else {
                binding.tvDownNoArrivalDataMessage.visibility = View.VISIBLE
                binding.tvDownNoArrivalDataMessage.isSelected = true

                binding.tvDownFirstArrivalStation.visibility = View.INVISIBLE
                binding.tvDownFirstArrivalTime.visibility = View.INVISIBLE

                binding.tvDownSecondArrivalStation.visibility = View.INVISIBLE
                binding.tvDownSecondArrivalTime.visibility = View.INVISIBLE
            }


//            binding.tvUpDirection.text = item.destination
//            binding.tvDownDirection.text = item.destination

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

    // 호선별 커스텀 설정
    private fun setLineCustom(
        lineName: String,
        binding: ItemNearbyNotificationBinding,
        context: Context
    ) {
        val lineColorResId = when (lineName) {
            "1호선" -> R.color.line_1
            "2호선" -> R.color.line_2
            "3호선" -> R.color.line_3
            "4호선" -> R.color.line_4
            "5호선" -> R.color.line_5
            "6호선" -> R.color.line_6
            "7호선" -> R.color.line_7
            "8호선" -> R.color.line_8
            "9호선" -> R.color.line_9
            "분당선" -> R.color.line_bundang
            "신분당선" -> R.color.line_sinbundang
            "우이신설선" -> R.color.line_ui_sinseol
            "경의중앙선" -> R.color.line_gyeongui_jungang
            "공항철도" -> R.color.line_airport_rail_link
            "경춘선" -> R.color.line_gyeongchun
            else -> R.color.gray_400
        }

        val color = ContextCompat.getColor(context, lineColorResId)
        binding.btnLineNumber.backgroundTintList = ColorStateList.valueOf(color)

        binding.btnLineNumber.text = getLineDisplayName(lineName)
    }

}