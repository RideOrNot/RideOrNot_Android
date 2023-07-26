package com.hanium.rideornot.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R

class SearchResultRecyclerViewAdapter(
    private var itemList: List<SearchResultModel>,
    private var searchRecyclerViewInterface: ISearchResultRecyclerView
) :
    RecyclerView.Adapter<SearchResultRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result_recycler_ex, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
        Log.d("onBindViewHolder", "is called")
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val searchResult: TextView = itemView.findViewById(R.id.tv_search_result)
        private val constraintSearchItem: ConstraintLayout =
            itemView.findViewById(R.id.cl_search_item)

        init {
            // 리스너 연결
            constraintSearchItem.setOnClickListener(this)
        }

        fun bind(item: SearchResultModel) {
            searchResult.text = item.stationName
        }

        override fun onClick(view: View?) {
            when (view) {
                constraintSearchItem -> {
                    searchRecyclerViewInterface.onItemClick(adapterPosition)
                }
            }
        }


    }

}