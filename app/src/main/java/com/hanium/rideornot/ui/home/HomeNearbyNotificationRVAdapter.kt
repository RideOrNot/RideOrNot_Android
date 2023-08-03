package com.hanium.rideornot.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.data.response.Arrival
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.databinding.ItemNearbyNotificationBinding
import com.hanium.rideornot.domain.LineDao
import com.hanium.rideornot.domain.StationDao
import com.hanium.rideornot.domain.StationDatabase

class HomeNearbyNotificationRVAdapter(
    context: Context,
    private var arrivalList: ArrayList<Arrival>
) :
    RecyclerView.Adapter<HomeNearbyNotificationRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(arrival: ArrivalResponse)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun updateData(newArrivalList: ArrayList<Arrival>) {
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
        fun bind(item: Arrival) {
            setLineCustom(item.lineId, binding, context)

            binding.btnLineNumber.text = item.lineId
//            binding.tvUpDirection.text = item.destination
//            binding.tvDownDirection.text = item.destination

            binding.tvUpFirstArrivalStation.text = item.destination
            binding.tvUpFirstArrivalTime.text = item.destination

            binding.tvUpSecondArrivalStation.text = item.destination
            binding.tvUpSecondArrivalTime.text = item.destination

            (item.arrivalTime.toString() + "분").also { binding.tvUpFirstArrivalTime.text = it }
            (item.arrivalTime.toString() + "분").also { binding.tvUpSecondArrivalTime.text = it }

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