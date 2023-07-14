package com.hanium.rideornot.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.data.ArrivalResponse
import com.hanium.rideornot.databinding.ItemNearbyNotificationBinding

class HomeNearbyNotificationRVAdapter(private var arrivalList: ArrayList<ArrivalResponse>) :
    RecyclerView.Adapter<HomeNearbyNotificationRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(arrival: ArrivalResponse)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
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

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrivalList[position])
    }

    override fun getItemCount(): Int {
        return arrivalList.size
    }

    inner class ViewHolder(val binding: ItemNearbyNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArrivalResponse) {
            binding.btnLineNumber.text = item.lineName
            binding.tvUpDirection.text = item.destination
            binding.tvDownDirection.text = item.destination

            (item.arrivalTime.toString() + "분").also { binding.tvUpArrivalTimeContent.text = it }
            (item.arrivalTime.toString() + "분").also { binding.tvDownArrivalTimeContent.text = it }

        }
    }

}