package com.hanium.rideornot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.databinding.FragmentFavoriteBinding
import com.hanium.rideornot.search.ISearchHistoryRecyclerView
import com.hanium.rideornot.search.SearchHistoryModel
import com.hanium.rideornot.search.SearchHistoryRecyclerViewAdapter

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

        binding.editTextSearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action== KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터가 눌릴 때 동작
                historyList.add(SearchHistoryModel(id=-1, binding.editTextSearch.text.toString()))
                searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
                hideKeyboard()
                handleSearch()
                binding.editTextSearch.text.clear()
                true
            } else {
                false
            }
        }
        binding.constraintLayout.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }
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


    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun handleSearch() {

    }



}