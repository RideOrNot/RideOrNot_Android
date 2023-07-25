package com.hanium.rideornot.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.UiThread
import com.hanium.rideornot.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.databinding.FragmentSearchBinding
import com.hanium.rideornot.search.ISearchHistoryRecyclerView
import com.hanium.rideornot.search.ISearchResultRecyclerView
import com.hanium.rideornot.search.SearchHistoryModel
import com.hanium.rideornot.search.SearchHistoryRecyclerViewAdapter
import com.hanium.rideornot.search.SearchResultModel
import com.hanium.rideornot.search.SearchResultRecyclerViewAdapter
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class SearchFragment : Fragment(),
    OnMapReadyCallback,
    ISearchHistoryRecyclerView,
    ISearchResultRecyclerView {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter
    private lateinit var searchResultRecyclerViewAdapter: SearchResultRecyclerViewAdapter
    private val historyList = mutableListOf<SearchHistoryModel>() // TODO: 검색기록 저장방식 추후 모색
    private val coroutineScope : CoroutineScope = CoroutineScope(Dispatchers.Main)

    private val tempStations = listOf(
        SearchResultModel(id = 1, stationName = "서울역"),
        SearchResultModel(id = 3, stationName = "합정역"),
        SearchResultModel(id = 4, stationName = "서울울역"),
        SearchResultModel(id = 2, stationName = "서울서울역"),
        SearchResultModel(id = 5, stationName = "서서울역"),
        SearchResultModel(id = 6, stationName = "서울 역"),
        SearchResultModel(id = 7, stationName = "서울 역서울"),
        SearchResultModel(id = 8, stationName = "서울 역 서 울역"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // TODO: 검색 버튼을 안 눌러도 실시간으로 검색결과가 뜨게 하고 싶은 경우, 이 메서드 사용
//        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//        })
        
        binding.recyclerView.setHasFixedSize(true)

        binding.editTextSearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터가 눌릴 때 동작
                historyList.add(
                    SearchHistoryModel(
                        id = -1,
                        binding.editTextSearch.text.toString()
                    )
                ) // TODO: ID값 수정
                searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
                hideKeyboard()
                handleSearch(tempStations)
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
        binding.editTextSearch.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.recyclerView.adapter = searchHistoryRecyclerViewAdapter
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
        searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {

    }

    override fun onItemDeleteClick(position: Int) {
        historyList.removeAt(position)
        searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
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

    private fun handleSearch(list: List<SearchResultModel>) {
        val text: String = binding.editTextSearch.text.toString()
        val res: MutableList<SearchResultModel> = mutableListOf()
        if (text == "") {
            return
        }
        for (element in list) {
            var matchCnt = 0
            var index = 0
            while (index < element.stationName.length) {
                if (element.stationName[index] == text[0]) {
                    for (i in text) {
                        if (index >= element.stationName.length) {
                            break
                        }
                        if (i == element.stationName[index]) {
                            index++
                            matchCnt++
                        } else {
                            index++
                            matchCnt = 0
                            break
                        }
                    }
                } else {
                    index++
                }
                if (matchCnt == text.length) {
                    res.add(element)
                    break
                }
            }
        }
        searchResultRecyclerViewAdapter = SearchResultRecyclerViewAdapter(res, this)
        binding.recyclerView.adapter = searchResultRecyclerViewAdapter
    }


}