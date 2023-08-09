package com.hanium.rideornot.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            val upDirectionList = item.filter { it.direction in listOf("상행", "외선") }
            val downDirectionList = item.filter { it.direction in listOf("하행", "내선") }

            // 각 방향별로 가장 빠른 2개의 도착 정보 선택 (arrivalTime이 0인 경우는 제외)
            val fastestUpDirection = upDirectionList.filter { it.arrivalTime != 0 }
                .sortedBy { it.arrivalTime }
                .take(2)

            val fastestDownDirection = downDirectionList.filter { it.arrivalTime != 0 }
                .sortedBy { it.arrivalTime }
                .take(2)

            // 도착 정보에 따라 뷰 설정
            updateVisibility(
                fastestUpDirection,
                binding.tvUpNoArrivalDataMessage,
                binding.tvUpFirstArrivalStation, binding.tvUpFirstArrivalTime,
                binding.tvUpSecondArrivalStation, binding.tvUpSecondArrivalTime
            )

            updateVisibility(
                fastestDownDirection,
                binding.tvDownNoArrivalDataMessage,
                binding.tvDownFirstArrivalStation, binding.tvDownFirstArrivalTime,
                binding.tvDownSecondArrivalStation, binding.tvDownSecondArrivalTime
            )


//            binding.tvUpDirection.text = item.destination
//            binding.tvDownDirection.text = item.destination

        }
    }

    /**
     * 도착 정보에 따라 뷰의 visibility 및 텍스트 설정
     * @param direction 해당 방향의 도착 정보 리스트
     * @param noDataView 데이터 없을 때 보여줄 뷰
     * @param arrivalViews 도착 정보를 보여줄 뷰들
     */
    fun updateVisibility(
        direction: List<Arrival>,
        noDataView: View,
        vararg arrivalViews: View
    ) {
        noDataView.visibility = if (direction.isEmpty()) View.VISIBLE else View.INVISIBLE

        val arrivalCount = direction.size.coerceAtMost(arrivalViews.size / 2)
        for (index in 0 until arrivalCount) {
            val (stationView, timeView) = arrivalViews[index * 2] as TextView to arrivalViews[index * 2 + 1] as TextView
            val arrival = direction[index]

            stationView.text = arrival.destination.substringBefore("행")
            timeView.text = if (arrival.arrivalTime / 60 == 0) "곧 도착" else "${arrival.arrivalTime / 60}분"

            stationView.visibility = View.VISIBLE
            timeView.visibility = View.VISIBLE
        }

        for (index in arrivalCount until arrivalViews.size / 2) {
            val (stationView, timeView) = arrivalViews[index * 2] as TextView to arrivalViews[index * 2 + 1] as TextView
            stationView.visibility = View.INVISIBLE
            timeView.visibility = View.INVISIBLE
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