package com.hanium.rideornot.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.data.ArrivalResponse
import com.hanium.rideornot.databinding.ItemLastStationBinding

class HomeLastStationRVAdapter(private var arrivalList: ArrayList<ArrivalResponse>) :
    RecyclerView.Adapter<HomeLastStationRVAdapter.ViewHolder>() {

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
        val binding: ItemLastStationBinding =
            ItemLastStationBinding.inflate(
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

    inner class ViewHolder(val binding: ItemLastStationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArrivalResponse) {
            binding.tvLineNumber.text = item.lineName
        }
    }

}