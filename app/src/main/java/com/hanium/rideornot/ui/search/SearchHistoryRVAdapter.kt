package com.hanium.rideornot.ui.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.ui.SearchViewModel
import com.hanium.rideornot.utils.methods.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchHistoryRVAdapter(
    private val context: Context,
    var itemList: List<SearchHistory>,
    private val searchRecyclerViewInterface: ISearchHistoryRV,
    private val searchViewModel: SearchViewModel
) :
    RecyclerView.Adapter<SearchHistoryRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_history_recycler_ex, parent, false)
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
        private val tvSearchHistory: TextView = itemView.findViewById(R.id.tv_search_result)
        private val ivDeleteSearchBtn: ImageView = itemView.findViewById(R.id.iv_delete_search_history_btn)
        private val clSearchItem: ConstraintLayout =
            itemView.findViewById(R.id.cl_search_item)
        private val llLines: LinearLayout = itemView.findViewById(R.id.ll_lines)

        init {
            // 리스너 연결
            ivDeleteSearchBtn.setOnClickListener(this)
            clSearchItem.setOnClickListener(this)
        }

        fun bind(item: SearchHistory) {
            tvSearchHistory.text = item.stationName
            clSearchItem.setOnClickListener {
                searchRecyclerViewInterface.onSearchHistoryItemClick(item.stationName)
            }
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
            }
        }

        override fun onClick(view: View?) {
            when (view) {
                ivDeleteSearchBtn -> {
                    searchRecyclerViewInterface.onSearchHistoryItemDeleteClick(adapterPosition)
                }
            }
        }


    }

}