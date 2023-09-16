package com.hanium.rideornot.ui.favorite

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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.ItemFavoriteEditBinding
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.ui.search.SearchViewModel
import com.hanium.rideornot.utils.methods.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FavoriteEditRVAdapter(
    private val context: Context,
    var itemList: MutableList<Favorite>,
    private val searchViewModel: SearchViewModel,
) :
    RecyclerView.Adapter<FavoriteEditRVAdapter.ViewHolder>(),
    FavoriteItemTouchHelperCallback.OnItemMoveListener {

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemFavoriteEditBinding =
            ItemFavoriteEditBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(val binding: ItemFavoriteEditBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private val tvStationName: TextView = binding.tvStationName
        private val tvDeleteBtn: TextView = binding.tvDeleteBtn
        private val llLines: LinearLayout = itemView.findViewById(R.id.ll_lines)

        init {
            // 리스너 연결
            tvDeleteBtn.setOnClickListener(this)
        }

        fun bind(item: Favorite) {
            tvStationName.text = item.stationName
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
                tvDeleteBtn -> {
                    itemList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    Log.d("onDeleteClick","activated")
                }
            }
        }
    }
}