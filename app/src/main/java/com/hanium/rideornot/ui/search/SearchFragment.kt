package com.hanium.rideornot.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.hanium.rideornot.domain.StationDatabase
import com.hanium.rideornot.ui.SearchHistoryViewModel
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
    ISearchResultRV {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchHistoryRVAdapter: SearchHistoryRVAdapter
    private lateinit var searchResultRVAdapter: SearchResultRVAdapter
    private lateinit var searchHistoryViewModel: SearchHistoryViewModel
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private val tempStations = listOf(
        SearchResultModel(id = 1, stationId = -1, stationName = "서울역"),
        SearchResultModel(id = 3, stationId = -1, stationName = "합정역"),
        SearchResultModel(id = 4, stationId = -1, stationName = "서울울역"),
        SearchResultModel(id = 2, stationId = -1, stationName = "서울서울역"),
        SearchResultModel(id = 5, stationId = -1, stationName = "서서울역"),
        SearchResultModel(id = 6, stationId = -1, stationName = "서울 역"),
        SearchResultModel(id = 7, stationId = -1, stationName = "서울 역서울"),
        SearchResultModel(id = 8, stationId = -1, stationName = "서울 역 서 울역"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // TODO: 검색 버튼을 안 눌러도 실시간으로 검색결과가 뜨게 하고 싶은 경우, 이 메서드 사용
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
        searchHistoryViewModel = SearchHistoryViewModel(requireContext())

        binding.recyclerView.setHasFixedSize(true)

        binding.editTextSearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터가 눌릴 때 동작
                // TODO: 일치하는 Station 객체를 직접 추가하도록 변경
                searchHistoryViewModel.insertSearchHistory(
                    SearchHistory(
                        stationId = -1,
                        stationName = binding.editTextSearch.text.toString()
                    )
                ) // TODO: ID값 수정
                searchHistoryRVAdapter.notifyDataSetChanged()
                hideKeyboard()
                handleSearch()
                binding.editTextSearch.text.clear()
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
//        binding.editTextSearch.setOnTouchListener { view, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                binding.recyclerView.adapter = searchHistoryRVAdapter
//            }
//            false
//        }

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
        searchHistoryViewModel.searchHistoryList.observe(
            viewLifecycleOwner,
            Observer { searchHistoryList ->
                // 데이터가 로드된 후에 RecyclerView를 초기화
                searchHistoryRVAdapter = SearchHistoryRVAdapter(searchHistoryList, this)
                initRecycler()
            })
    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        searchHistoryRVAdapter =
            SearchHistoryRVAdapter(searchHistoryViewModel.searchHistoryList.value!!, this)
        searchHistoryRVAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {

    }

    override fun onItemDeleteClick(position: Int) {
        val searchHistoryToDelete = searchHistoryRVAdapter.itemList[position]
        searchHistoryViewModel.deleteSearchHistory(searchHistoryToDelete)
        searchHistoryRVAdapter.notifyDataSetChanged()
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
                val searchResult = stationDao.findStationsByName(searchQuery)
                searchResultRVAdapter = SearchResultRVAdapter(searchResult, this@SearchFragment)
                binding.recyclerView.adapter = searchResultRVAdapter
            }
        }

    }


}