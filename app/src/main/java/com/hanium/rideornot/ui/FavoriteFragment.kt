package com.hanium.rideornot.ui

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.databinding.FragmentFavoriteBinding
import com.hanium.rideornot.search.ISearchHistoryRecyclerView
import com.hanium.rideornot.search.ISearchResultRecyclerView
import com.hanium.rideornot.search.SearchHistoryModel
import com.hanium.rideornot.search.SearchHistoryRecyclerViewAdapter
import com.hanium.rideornot.search.SearchResultModel
import com.hanium.rideornot.search.SearchResultRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

//        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    

}