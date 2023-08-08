package com.hanium.rideornot.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.UiThread
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.hanium.rideornot.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.databinding.FragmentSearchBinding
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.domain.StationDatabase
import com.hanium.rideornot.ui.SearchViewModel
import com.hanium.rideornot.ui.StationDetailFragment
import com.hanium.rideornot.ui.home.HomeFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchFragment : Fragment(),
    OnMapReadyCallback,
    ISearchHistoryRV,
    ISearchResultRV,
    SearchResultRVAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchHistoryRVAdapter: SearchHistoryRVAdapter
    private lateinit var searchResultRVAdapter: SearchResultRVAdapter
    private lateinit var searchViewModel: SearchViewModel
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // 검색어 입력을 실시간으로 탐지하여 검색 결과에 반영
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString() ?: ""
                if (searchText.isNotEmpty()) {
                    handleSearch()
                } else {
                    binding.recyclerView.adapter = searchHistoryRVAdapter
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        searchViewModel = SearchViewModel(requireContext())
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.setHasFixedSize(true)

        binding.editTextSearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터가 눌릴 때 동작
                // TODO: 일치하는 Station 객체를 직접 추가하도록 변경
                coroutineScope.launch {
                    searchViewModel.insertSearchHistory(
                        SearchHistory(
                            stationId = -1,
                            stationName = binding.editTextSearch.text.toString()
                        )
                    ) // TODO: ID값 수정\
                    hideKeyboard()
                    handleSearch()
                    binding.editTextSearch.text.clear()
                }
                true
            } else {
                false
            }
        }
        // TODO: 검색창 바깥부분 터치 시 키보드 내려가는 기능 동작 안하는 문제 수정
        binding.constraintLayout.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }
        binding.fcvMap.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

        initView()

        return binding.root
    }

    private fun initView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fcv_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fcv_map, it).commit()
            }

        // 비동기로 NaverMap 객체 얻기
        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // ...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.searchHistoryList.observe(this, Observer {
            initSearchHistoryRecycler()
            binding.recyclerView.adapter = searchHistoryRVAdapter
            searchHistoryRVAdapter.notifyDataSetChanged()
        })
    }

    private fun initSearchHistoryRecycler() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        searchHistoryRVAdapter =
            SearchHistoryRVAdapter(searchViewModel.searchHistoryList.value!!, this)
        searchHistoryRVAdapter.notifyDataSetChanged()
    }

    override fun onSearchResultItemClick(station: Station) {
        switchToStationDetailFragment(station)
    }

    override fun onSearchHistoryItemClick(position: Int) {

    }

    override fun onSearchHistoryItemDeleteClick(position: Int) {
        coroutineScope.launch {
            val searchHistoryToDelete = searchHistoryRVAdapter.itemList[position]
            searchViewModel.deleteSearchHistory(searchHistoryToDelete)
            searchHistoryRVAdapter.notifyDataSetChanged()
        }
    }


    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun handleSearch() {
        val searchQuery: String = binding.editTextSearch.text.toString()
        if (searchQuery.isNotEmpty()) {
            val stationDao = StationDatabase.getInstance(requireContext())!!.stationDao()
            lifecycleScope.launch {
                val searchResult = stationDao.findStationsByName(searchQuery).distinctBy { it.stationName }
                searchResultRVAdapter = SearchResultRVAdapter(
                    requireContext(),
                    searchResult,
                    searchViewModel,
                    this@SearchFragment,
                    this@SearchFragment
                )
                binding.recyclerView.adapter = searchResultRVAdapter
            }
        }
    }

    override fun onItemClick(position: Int) {
    }

    private fun switchToStationDetailFragment(station: Station) {
        Log.d("SwitchToStationDetailFragment", "Succeeded, station: ${station.stationName}")
        val stationDetailFragment = StationDetailFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frm_main, stationDetailFragment)
            .commit()
    }

}