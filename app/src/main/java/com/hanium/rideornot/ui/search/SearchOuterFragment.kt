package com.hanium.rideornot.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSearchOuterBinding

class SearchOuterFragment : Fragment() {

    private lateinit var binding: FragmentSearchOuterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchOuterBinding.inflate(inflater, container, false)
        // inner fragment에서 사라지게 했던 bnv를 다시 활성화함. 조금 원시적인 방법이긴 한데 추후 더 나은 방법이 생각나면 바꾸기
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bnv_main)
        bottomNavigationView.visibility = View.VISIBLE

        binding.tvSearch.setOnClickListener {
            handleTextViewClick()
        }

        return binding.root
    }

    private fun handleTextViewClick() {
        findNavController().navigate(
            SearchOuterFragmentDirections.actionFragmentOuterSearchToFragmentInnerSearch()
        )
    }


}