package com.hanium.rideornot.search


interface ISearchHistoryRecyclerView {

    // 검색 아이템 삭제 버튼 클릭
    fun onItemDeleteClicked(position: Int)

    // 검색 버튼 클릭
    fun onItemClicked(position: Int)

}