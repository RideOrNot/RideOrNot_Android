package com.hanium.rideornot.ui.favorite

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.hanium.rideornot.R
import kotlin.math.max
import kotlin.math.min

class FavoriteItemTouchHelperCallback(private val recyclerViewAdapter: FavoriteEditRVAdapter) : ItemTouchHelper.Callback() {

    interface OnItemMoveListener {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }

    // swipe_view 를 swipe 했을 때 <삭제> 화면이 보이도록 고정하기 위한 변수들
    private var currentPosition: Int? = null    // 현재 선택된 recycler view의 position
    private var previousPosition: Int? = null   // 이전에 선택했던 recycler view의 position
    private var currentDx = 0f                  // 현재 x 값
    private var clampSize = 0f                      // 고정시킬 크기

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
        recyclerViewAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    // 아이템이 스와이프될 때 호출
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
//            Log.d("onselectedChange, currentPosition set", currentPosition.toString())
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    // 사용자가 view를 swipe 했다고 간주할 최소 속도 정하기
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = defaultValue * 10

    // 사용자가 view를 swipe 했다고 간주하기 위해 이동해야하는 부분 반환
    // (사용자가 손을 떼면 호출됨)
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        // -clamp 이상 swipe시 isClamped를 true로 변경 아닐시 false로 변경
        setTag(viewHolder, currentDx <= -clampSize)
        return 2f
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
            val isClamped = getTag(viewHolder)      // 고정할지 말지 결정, true : 고정함 false : 고정 안 함
            val newX = clampViewPositionHorizontal(dX, isClamped, isCurrentlyActive)  // newX 만큼 이동(고정 시 이동 위치/고정 해제 시 이동 위치 결정)

//            Log.d("newX", newX.toInt().toString())
//            Log.d("clampSize", clampSize.toInt().toString())
            // 고정시킬 시 애니메이션 추가
            if (newX == -clampSize) {
                //getView(viewHolder).animate().translationX(-clampSize).setDuration(1000L).start()
                getView(viewHolder).translationX = -clampSize
                return
            }

            currentDx = newX
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

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean = viewHolder.itemView.tag as? Boolean ?: false
    fun setClamp(clamp: Float) {
        this.clampSize = clamp
    }

    private fun clampViewPositionHorizontal(
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        // RIGHT 방향으로 swipe 막기
        val max = 0f

        // 고정할 수 있으면
        val newX = if (isClamped) {
            // 현재 swipe 중이면 swipe되는 영역 제한
            if (isCurrentlyActive)
                    dX - clampSize
            // swipe 중이 아니면 고정시키기
            else -clampSize
        }
        else dX

        // newX가 0보다 크지 않은지, 혹은 -clampSize보다 작지 않은지 확인
        // onChildDraw() 의 if(newX == -clampSize) 부분에서, float 자료형의 연산 오차 때문에
        // -clampSize 와 newX의 수치상 값이 동일함에도 if문이 false를 반환하는 문제가 있었음.
        // (-clampSize.toInt() , newX.toInt() 로 비교해보니 전자는 -180, 후자는 -181이 나온 것을 확인함)
        // 따라서 -clampSize 가 아닌 -clampSize-1 을 반환값으로 주어 임시 조치하였음.
        return max(min(newX, max), -clampSize -1)
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View = viewHolder.itemView.findViewById(R.id.cl_favorite_body)
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f                                      // 현재 x 위치 초기화
        previousPosition = viewHolder.adapterPosition       // 드래그 또는 스와이프 동작이 끝난 view의 position 기억하기
        getDefaultUIUtil().clearView(getView(viewHolder))
    }


    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        // 현재 선택한 view가 이전에 선택한 view와 같으면 패스
        if (currentPosition == previousPosition) return

        // 이전에 선택한 위치의 view 고정 해제
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).animate().x(0f).setDuration(100L).start()
            setTag(viewHolder, false)
            previousPosition = null
        }

    }

}