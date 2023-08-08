package com.hanium.rideornot.ui.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.Log
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
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.ui.SearchViewModel
import com.hanium.rideornot.utils.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultRVAdapter(
    private var context: Context,
    private var itemList: List<Station>,
    private var searchViewModel: SearchViewModel,
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
        //private val lineBtn: AppCompatButton = itemView.findViewById(R.id.btn_line)
        private val constraintSearchItem: ConstraintLayout =
            itemView.findViewById(R.id.cl_search_item)
        private val lineLinearLayout: LinearLayout = itemView.findViewById(R.id.ll_lines)

        init {
            // 리스너 연결
            constraintSearchItem.setOnClickListener(this)
        }

        fun bind(item: Station) {
            CoroutineScope(Dispatchers.Main).launch {
                val lineIdList = searchViewModel.findLinesByStationName(item.stationName)
                for (lineId in lineIdList) {
                    val lineItemView = LayoutInflater.from(context).inflate(R.layout.item_line, lineLinearLayout, false)
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
                stationNameTextView.text = item.stationName
            }
        }

        override fun onClick(view: View?) {
            when (view) {
                constraintSearchItem -> {
                    searchRecyclerViewInterface.onSearchHistoryItemClick(adapterPosition)
                }
            }
        }


    }

}