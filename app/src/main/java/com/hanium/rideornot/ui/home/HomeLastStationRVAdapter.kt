package com.hanium.rideornot.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.ItemLastStationBinding
import com.hanium.rideornot.domain.Station

class HomeLastStationRVAdapter(private var stationList: ArrayList<Station>) :
    RecyclerView.Adapter<HomeLastStationRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(station: Station)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun updateData(newArrivalList: ArrayList<Station>) {
        stationList = newArrivalList
        notifyDataSetChanged()
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
        holder.bind(stationList[position])
    }

    override fun getItemCount(): Int {
        return stationList.size
    }

    inner class ViewHolder(val binding: ItemLastStationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Station) {
//            binding.tvLineNumber.text = item.lineId.toString()
            binding.tvStationName.text = item.stationName
        }
    }

}