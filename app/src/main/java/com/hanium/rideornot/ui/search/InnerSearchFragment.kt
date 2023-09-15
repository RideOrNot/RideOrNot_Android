package com.hanium.rideornot.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSearchInnerBinding
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.domain.StationDatabase
import com.hanium.rideornot.ui.common.ViewModelFactory
import kotlinx.coroutines.*


class InnerSearchFragment : Fragment(),
    ISearchHistoryRV,
    ISearchResultRV {
    private lateinit var binding: FragmentSearchInnerBinding
    private lateinit var searchHistoryRVAdapter: SearchHistoryRVAdapter
    private lateinit var searchResultRVAdapter: SearchResultRVAdapter
    private val searchViewModel: SearchViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private fun setBackBtnHandling() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun handleSwitchToSearchHistory(searchHistoryList: List<SearchHistory>) {
        initSearchHistoryRecycler(searchHistoryList)
        binding.rvSearch.adapter = searchHistoryRVAdapter
        binding.tvRecentSearch.text = "최근 검색"
    }

    private fun handleSwitchToSearchResult() {
        handleSearch()
        binding.tvRecentSearch.text = "검색 결과"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchInnerBinding.inflate(inflater, container, false)
        // searchInnerFragment 접근 시 bnv 사라지게 설정 (inner fragment에서 bnv로 fragment 전환 시 이후 search fragment에 접근 시 
        // innerFragment가 출력되는 버그가 발생하였음. 이를 방지하기 위해 inner fragment에서 bnv를 사라지게 설정하였음.
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bnv_main)
        bottomNavigationView.visibility = View.GONE
        setBackBtnHandling()

        binding.rvSearch.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearch.setHasFixedSize(true)

        // editText에 부착되어 있는 뒤로가기 버튼 클릭 시 동작
        binding.ivPrev.setOnClickListener {
            switchToOuterSearchFragment()
        }

        // 키보드가 자동으로 올라가게 설정
        showKeyboard(binding.editTextSearch)
        binding.editTextSearch.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터가 눌릴 때 동작
                coroutineScope.launch {
                    hideKeyboard()
                    handleSearch()
                    //binding.editTextSearch.text.clear()
                }
                true
            } else {
                false
            }
        }
        // TODO: 검색창 바깥부분 터치 시 키보드 내려가는 기능 완성하기
        binding.constraintLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }

//    override fun onPause() {`
//        super.onPause()
//        // Fragment 전환 시 입력했던 텍스트가 사라지는 기능
//        binding.editTextSearch.text.clear()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.searchHistoryList.observe(this) {
            // searchResult 아이템 클릭 시 serachHistory 리사이클러뷰로 교체되어 버려서, 검색어가 입력된 상태면 리사이클러뷰를 교체하지 않도록 설정
            if (binding.editTextSearch.text.isEmpty()) {
                handleSwitchToSearchHistory(it)
            } else {
                initSearchHistoryRecycler(it)
            }
        }

        // 검색어 입력을 실시간으로 탐지하여 검색 결과에 반영
        binding.editTextSearch.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val searchText = s?.toString() ?: ""
                    if (searchText.isNotEmpty()) {
                        handleSwitchToSearchResult()
                        binding.ivClear.setImageResource(R.drawable.ic_delete)
                        binding.ivClear.setOnClickListener {
                            binding.editTextSearch.text.clear()
                        }
                    } else {
                        handleSwitchToSearchHistory(searchViewModel.searchHistoryList.value!!)
                        binding.ivClear.setImageResource(R.drawable.ic_search)
                        binding.ivClear.setOnClickListener(null)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
    }

    private fun initSearchHistoryRecycler(searchHistoryList: List<SearchHistory>) {
        searchHistoryRVAdapter =
            SearchHistoryRVAdapter(requireContext(), searchHistoryList, this, searchViewModel)
        searchHistoryRVAdapter.notifyDataSetChanged()
    }

    override fun onSearchResultItemClick(station: Station) {
        switchToStationDetailFragment(station.stationName)
    }

    override fun onSearchHistoryItemClick(stationName: String) {
        switchToStationDetailFragment(stationName)
    }

    override fun onSearchHistoryItemDeleteClick(position: Int) {
        coroutineScope.launch {
            val searchHistoryToDelete = searchHistoryRVAdapter.itemList[position]
            searchViewModel.deleteSearchHistory(searchHistoryToDelete)
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

    private fun showKeyboard(focusing: View) {
        if (activity != null) {
            focusing.requestFocus()
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // requestFocus(), showSoftInput() 사이에 살짝의 딜레이를 주어야 키보드 팝업 기능이 제대로 작동함
            focusing.postDelayed(java.lang.Runnable {
                focusing.requestFocus()
                inputManager.showSoftInput(focusing, 0)
            }, 100)
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
                    this@InnerSearchFragment
                )
                binding.rvSearch.adapter = searchResultRVAdapter
            }
        }
    }


    private fun switchToStationDetailFragment(stationName: String) {
        findNavController().navigate(
            InnerSearchFragmentDirections.actionFragmentInnerSearchToActivityStationDetail(stationName)
        )
    }

    private fun switchToOuterSearchFragment() {
        findNavController().navigate(
            InnerSearchFragmentDirections.actionFragmentInnerSearchToFragmentOuterSearch()
        )
    }
}