package com.hanium.rideornot.ui.favorite

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import kotlin.math.max
import kotlin.math.min

class ItemTouchHelperCallback(private val itemMoveListener: OnItemMoveListener) : ItemTouchHelper.Callback() {

    interface OnItemMoveListener {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }

    // swipe_view 를 swipe 했을 때 <삭제> 화면이 보이도록 고정하기 위한 변수들
    private var currentPosition: Int? = null    // 현재 선택된 recycler view의 position
    private var previousPosition: Int? = null   // 이전에 선택했던 recycler view의 position
    private var clampSize = 0f

    private var tempViewHolder: RecyclerView.ViewHolder? = null

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

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        // 선택된 항목의 투명도 변경
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.7f
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.itemView?.alpha = 1.0f
        }

        viewHolder?.let {
            // 현재 드래그 또는 스와이프 중인 view 의 position 기억
            currentPosition = viewHolder.adapterPosition
            Log.d("onselectedChange, currentPosition set", currentPosition.toString())
            //getDefaultUIUtil().onSelected(getView(it))
        }
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
        if (actionState == ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val newX = clampViewPositionHorizontal(dX)  // newX 만큼 이동(고정 시 이동 위치/고정 해제 시 이동 위치 결정)

            // 삭제 버튼이 나타날 공간의 너비 정의
            clampSize = view.width.toFloat() / 8

//            if (newX == clampSize && currentClampedPosition != viewHolder.adapterPosition) {
//                getView(recyclerView.findViewHolderForAdapterPosition(currentClampedPosition!!)!!).animate().x(0f)
//                    .setDuration(100L).start()
//                currentClampedPosition = viewHolder.adapterPosition
//                Log.d("clampSize max", "set currentPosition = ${viewHolder.adapterPosition}")
//            }

            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                newX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun clampViewPositionHorizontal(dX: Float): Float {
        val max = 0f         // RIGHT 방향으로 swipe 막기
        return max(min(dX, max), -clampSize) // dX가 0보다 크지 않은지, 혹은 clampSize보다 작지 않은지 확인
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View = viewHolder.itemView.findViewById(R.id.cl_favorite_body)
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // 드래그 또는 스와이프 동작이 끝난 view의 position 기억하기
        previousPosition = viewHolder.adapterPosition
        Log.d("clearView, prevPosition set", previousPosition.toString())
        //getDefaultUIUtil().clearView(getView(viewHolder))
    }


    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        // 현재 선택한 view가 이전에 선택한 view와 같으면 패스
//        if (currentPosition == previousPosition) return
//
//        // 이전에 선택한 위치의 view 고정 해제
//        previousPosition?.let {
//            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
//            getView(viewHolder).animate().x(0f).setDuration(100L).start()
//            previousPosition = null
//            Log.d("prevPosition removed", "null")
//        }

    }

}