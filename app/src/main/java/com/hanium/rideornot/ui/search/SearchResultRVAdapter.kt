package com.hanium.rideornot.ui.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.ui.SearchViewModel
import com.hanium.rideornot.utils.methods.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultRVAdapter(
    private val context: Context,
    private val itemList: List<Station>,
    private val searchViewModel: SearchViewModel,
    private val searchRecyclerViewInterface: ISearchResultRV,
) :
    RecyclerView.Adapter<SearchResultRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

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
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val tvSearchResult: TextView = itemView.findViewById(R.id.tv_search_result)
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
                    val lineItemView = LayoutInflater.from(context).inflate(R.layout.item_line, llLines, false)
                    lineItemView.id = View.generateViewId()
                    val lineBtn = lineItemView.findViewById<AppCompatButton>(R.id.btn_line)
                    lineBtn.text = searchViewModel.getLineNameByLineId(lineId).firstOrNull().toString()
                    val color = ContextCompat.getColor(context, getLineColorIdByLineId(lineId))
                    lineBtn.backgroundTintList = ColorStateList.valueOf(color)
                    val backgroundDrawable = lineBtn.background as? GradientDrawable
                    backgroundDrawable?.setStroke(4, color)
                    lineBtn.background = backgroundDrawable
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
                        searchRecyclerViewInterface.onSearchResultItemClick(clickedItem)
                        searchViewModel.insertSearchHistory(
                            SearchHistory(
                                stationId = clickedItem.stationId,
                                stationName = clickedItem.stationName
                            )
                        )
                    }
                }

            }
        }


    }

}