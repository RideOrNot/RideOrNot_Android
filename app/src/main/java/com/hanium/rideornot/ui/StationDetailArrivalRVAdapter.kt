package com.hanium.rideornot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.data.ArrivalResponse
import com.hanium.rideornot.databinding.ItemStationDetailArrivalBinding

class StationDetailArrivalRVAdapter(private var arrivalList: ArrayList<ArrivalResponse>) :
    RecyclerView.Adapter<StationDetailArrivalRVAdapter.ViewHolder>() {

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
        val binding: ItemStationDetailArrivalBinding =
            ItemStationDetailArrivalBinding.inflate(
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

    inner class ViewHolder(val binding: ItemStationDetailArrivalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArrivalResponse) {
            binding.tvFirstArrivalStation.text = item.lineName
            (item.arrivalTime.toString() + "분").also { binding.tvFirstArrivalTime.text = it }
            binding.tvSecondArrivalStation.text = item.lineName
            (item.arrivalTime.toString() + "분").also { binding.tvSecondArrivalTime.text = it }
        }
    }

}