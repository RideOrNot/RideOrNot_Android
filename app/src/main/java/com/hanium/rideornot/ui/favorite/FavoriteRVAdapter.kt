package com.hanium.rideornot.ui.favorite

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
import com.hanium.rideornot.databinding.ItemFavoriteBinding
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.ui.SearchViewModel
import com.hanium.rideornot.utils.methods.getLineColorIdByLineId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteRVAdapter(
    private val context: Context,
    var itemList: List<Favorite>,
    private val favoriteViewModel: FavoriteViewModel,
    private val searchViewModel: SearchViewModel

) :
    RecyclerView.Adapter<FavoriteRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick()
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

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

            }
        }

//    inner class ViewHolder(val binding: ItemFavoriteBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: Favorite) {
//            binding.tvStationName.text = item.stationName.toString()
//        }
//    }

}