package com.hanium.rideornot.ui.favorite

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R

class ItemTouchHelperCallback(private val itemMoveListener: OnItemMoveListener) : ItemTouchHelper.Callback() {

    interface OnItemMoveListener {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }

    // 드래그 방향과 드래그 이동을 정의
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    // 아이템이 움직일 때 호출
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        itemMoveListener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    // 아이템이 스와이프 될 때 호출
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
    
    // 스와이프 범위 제한
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        if (dX < 0) { // 왼쪽으로 스와이프할 때만 적용
            val halfWidth = itemView.width / 4
            if (dX < -halfWidth) {
                super.onChildDraw(c, recyclerView, viewHolder, -halfWidth.toFloat(), dY, actionState, isCurrentlyActive)
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
    
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.7f
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.itemView?.alpha = 1.0f
        }
    }
}