package com.hanium.rideornot.ui.search

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.utils.methods.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultRVAdapter(
    private val context: Context,
    private val itemList: List<Station>,
    private val searchViewModel: SearchViewModel,
    private val searchResultRVInterface: ISearchResultRV,
) :
    RecyclerView.Adapter<SearchResultRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val tvSearchResult: TextView = itemView.findViewById(R.id.tv_search_history)
        private val clSearchItem: ConstraintLayout = itemView.findViewById(R.id.cl_search_item)
        private val llLines: LinearLayout = itemView.findViewById(R.id.ll_lines)

        init {
            clSearchItem.setOnClickListener(this)
        }

        fun bind(item: Station) {
            CoroutineScope(Dispatchers.Main).launch {
                llLines.removeAllViews()
                val lineIdList = searchViewModel.findLinesByStationName(item.stationName)
                for (lineId in lineIdList) {
                    val lineItemView = LayoutInflater.from(context).inflate(R.layout.item_line_small, llLines, false)
                    lineItemView.id = View.generateViewId()
                    val ivLine = lineItemView.findViewById<AppCompatImageView>(R.id.iv_line)
                    val tvLine = lineItemView.findViewById<AppCompatTextView>(R.id.tv_line)
                    tvLine.text = searchViewModel.getLineNameByLineId(lineId).firstOrNull().toString()
                    lineItemView.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val clickedItem = itemList[position]
                            searchResultRVInterface.onSearchResultItemClick(clickedItem)
                            searchViewModel.insertSearchHistory(
                                SearchHistory(
                                    stationName = clickedItem.stationName
                                )
                            )
                        }
                    }
                    val color = ContextCompat.getColor(context, getLineColorIdByLineId(lineId))
                    ivLine.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    // lineItemView를 부모 view의 LinearLayout에 추가
                    val parentLayout = itemView.findViewById<LinearLayout>(R.id.ll_lines)
                    parentLayout.addView(lineItemView)
                }
                tvSearchResult.text = item.stationName
            }
        }

        override fun onClick(view: View?) {
            when (view) {
                clSearchItem -> {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val clickedItem = itemList[position]
                        searchResultRVInterface.onSearchResultItemClick(clickedItem)
                        searchViewModel.insertSearchHistory(
                            SearchHistory(
                                stationName = clickedItem.stationName
                            )
                        )
                    }
                }
            }

        }
    }


}


