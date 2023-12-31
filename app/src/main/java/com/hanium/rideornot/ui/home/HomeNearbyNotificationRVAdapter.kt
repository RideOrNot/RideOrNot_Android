package com.hanium.rideornot.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.data.response.Arrival
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.databinding.ItemNearbyNotificationBinding
import com.hanium.rideornot.utils.methods.getLineColorIdByLineName

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

            // 각 방향별로 가장 빠른 2개의 도착 정보 선택 (arrivalTime이 0인 경우도 포함)
            val fastestUpDirection = upDirectionList.sortedBy { it.arrivalTime }
                .take(2)

            val fastestDownDirection = downDirectionList.sortedBy { it.arrivalTime }
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

            // 방면
            if (fastestUpDirection.isNotEmpty())
                binding.tvUpDirection.text =
                    fastestUpDirection[0].destination.split(" - ")[1].split(" ")[0]
            if (fastestDownDirection.isNotEmpty())
                binding.tvDownDirection.text =
                    fastestDownDirection[0].destination.split(" - ")[1].split(" ")[0]
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
            "${arrival.arrivalTime / 60}분 ${arrival.arrivalTime % 60}초".also { timeView.text = it }

            stationView.visibility = View.VISIBLE
            timeView.visibility = View.VISIBLE
        }

        for (index in arrivalCount until arrivalViews.size / 2) {
            val (stationView, timeView) = arrivalViews[index * 2] as TextView to arrivalViews[index * 2 + 1] as TextView
            stationView.visibility = View.INVISIBLE
            timeView.visibility = View.INVISIBLE
        }
    }


    // 호선별 커스텀 설정
    private fun setLineCustom(
        lineName: String,
        binding: ItemNearbyNotificationBinding,
        context: Context
    ) {
        val lineColorResId = getLineColorIdByLineName(lineName)

        val color = ContextCompat.getColor(context, lineColorResId)
        binding.btnLineNumber.backgroundTintList = ColorStateList.valueOf(color)
    }

}