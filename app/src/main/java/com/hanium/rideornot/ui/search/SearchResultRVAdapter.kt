package com.hanium.rideornot.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.domain.LineDao
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.domain.StationDatabase

class SearchResultRVAdapter(
    private var itemList: List<Station>,
    private var searchRecyclerViewInterface: ISearchResultRV
) :
    RecyclerView.Adapter<SearchResultRVAdapter.ViewHolder>() {

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
        private val stationNameTextView: TextView = itemView.findViewById(R.id.tv_search_result)
        private val lineBtn: AppCompatButton = itemView.findViewById(R.id.btn_line)
        private val constraintSearchItem: ConstraintLayout =
            itemView.findViewById(R.id.cl_search_item)

        init {
            // 리스너 연결
            constraintSearchItem.setOnClickListener(this)
        }

        fun bind(item: Station) {
            stationNameTextView.text = item.stationName
            val lineId = item.lineId
            // TODO: LineDao에 연결할 방법을 찾아서 lineName을 집어넣기
            lineBtn.text = lineId.toString()
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