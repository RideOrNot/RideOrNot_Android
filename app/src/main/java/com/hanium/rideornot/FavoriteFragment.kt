package com.hanium.rideornot

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.databinding.FragmentFavoriteBinding
import com.hanium.rideornot.search.SearchAdapter
import com.hanium.rideornot.search.SearchModel

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var searchAdapter: SearchAdapter
    private val datas = mutableListOf<SearchModel>()

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
        datas.apply {
            add(SearchModel(search_result = "서울역"))
            add(SearchModel(search_result = "용산역"))
            add(SearchModel(search_result = "홍대입구역"))
            add(SearchModel(search_result = "상수역"))
            add(SearchModel(search_result = "서울역"))
            add(SearchModel(search_result = "용산역"))
            add(SearchModel(search_result = "홍대입구역"))
            add(SearchModel(search_result = "상수역"))
            add(SearchModel(search_result = "서울역"))
            add(SearchModel(search_result = "용산역"))
            add(SearchModel(search_result = "홍대입구역"))
            add(SearchModel(search_result = "상수역"))
            add(SearchModel(search_result = "서울역"))
            add(SearchModel(search_result = "용산역"))
            add(SearchModel(search_result = "홍대입구역"))
            add(SearchModel(search_result = "상수역"))
            add(SearchModel(search_result = "서울역"))
            add(SearchModel(search_result = "용산역"))
            add(SearchModel(search_result = "홍대입구역"))
            add(SearchModel(search_result = "상수역"))
        }
        Log.d("initRecycler", "data list : $datas")

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        searchAdapter = SearchAdapter(datas)
        binding.recyclerView.adapter = searchAdapter
        Log.d("activityName", "${requireActivity()}")
        searchAdapter.notifyDataSetChanged()

    }

}