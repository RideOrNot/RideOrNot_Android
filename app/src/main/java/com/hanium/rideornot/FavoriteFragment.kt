package com.hanium.rideornot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.databinding.FragmentFavoriteBinding
import com.hanium.rideornot.search.ISearchHistoryRecyclerView
import com.hanium.rideornot.search.SearchHistoryRecyclerViewAdapter
import com.hanium.rideornot.search.SearchHistoryModel

class FavoriteFragment : Fragment(),
    ISearchHistoryRecyclerView {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var searchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter
    private val historyList = mutableListOf<SearchHistoryModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        historyList.apply {
            add(SearchHistoryModel(id = 1, stationName = "서울역"))
            add(SearchHistoryModel(id = 2, stationName = "용산역"))
            add(SearchHistoryModel(id = 3, stationName = "홍대입구역"))
            add(SearchHistoryModel(id = 4, stationName = "상수역"))
            add(SearchHistoryModel(id = 5, stationName = "서울역"))
            add(SearchHistoryModel(id = 6, stationName = "용산역"))
            add(SearchHistoryModel(id = 7, stationName = "홍대입구역"))
            add(SearchHistoryModel(id = 8, stationName = "상수역"))
            add(SearchHistoryModel(id = 9, stationName = "서울역"))
            add(SearchHistoryModel(id = 10, stationName = "용산역"))
            add(SearchHistoryModel(id = 11, stationName = "홍대입구역"))
            add(SearchHistoryModel(id = 12, stationName = "상수역"))
            add(SearchHistoryModel(id = 13, stationName = "서울역"))
            add(SearchHistoryModel(id = 14, stationName = "용산역"))
            add(SearchHistoryModel(id = 15, stationName = "홍대입구역"))
            add(SearchHistoryModel(id = 16, stationName = "상수역"))
            add(SearchHistoryModel(id = 17, stationName = "서울역"))
            add(SearchHistoryModel(id = 18, stationName = "용산역"))
            add(SearchHistoryModel(id = 19, stationName = "홍대입구역"))
            add(SearchHistoryModel(id = 20, stationName = "상수역"))
        }
        Log.d("initRecycler", "data list : $historyList")

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(historyList, this)
        binding.recyclerView.adapter = searchHistoryRecyclerViewAdapter
        Log.d("activityName", "${requireActivity()}")
        searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onItemClicked(position: Int) {

    }

    override fun onItemDeleteClicked(position: Int) {
        historyList.removeAt(position)
        searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
    }




}