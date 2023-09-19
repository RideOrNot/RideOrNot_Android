package com.hanium.rideornot.ui.favorite

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
import com.hanium.rideornot.databinding.ItemFavoriteBinding
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.ui.search.SearchViewModel
import com.hanium.rideornot.utils.methods.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteRVAdapter(
    private val context: Context,
    var itemList: List<Favorite>,
    private val favoriteViewModel: FavoriteViewModel,
    private val searchViewModel: SearchViewModel,
    private val favoriteRVInterface: IFavoriteRV,
) :
    RecyclerView.Adapter<FavoriteRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemFavoriteBinding =
            ItemFavoriteBinding.inflate(
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

    inner class ViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private val tvStationName: TextView = binding.tvStationName
        private val clFavorite: ConstraintLayout = binding.root
        private val llLines: LinearLayout = itemView.findViewById(R.id.ll_lines)

        init {
            // 리스너 연결
            clFavorite.setOnClickListener(this)
        }

        fun bind(item: Favorite) {
            tvStationName.text = item.stationName
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
                            favoriteRVInterface.onFavoriteItemClick(clickedItem)
                        }
                    }
                    val color = ContextCompat.getColor(context, getLineColorIdByLineId(lineId))
                    ivLine.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    // lineItemView를 부모 view의 LinearLayout에 추가
                    val parentLayout = itemView.findViewById<LinearLayout>(R.id.ll_lines)
                    parentLayout.addView(lineItemView)
                }
            }
        }

        override fun onClick(view: View?) {
            when (view) {
                clFavorite -> {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val clickedItem = itemList[position]
                        favoriteRVInterface.onFavoriteItemClick(clickedItem)
                    }
                }
            }
        }


    }
}