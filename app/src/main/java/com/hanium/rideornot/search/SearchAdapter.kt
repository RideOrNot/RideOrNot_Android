package com.hanium.rideornot.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R

class SearchAdapter(private var itemList: MutableList<SearchModel>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_ex,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
        Log.d("onBindViewHolder", "is called")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val search_result: TextView = view.findViewById(R.id.search_result)

        fun bind(item: SearchModel) {
            search_result.text = item.search_result
        }
    }

}