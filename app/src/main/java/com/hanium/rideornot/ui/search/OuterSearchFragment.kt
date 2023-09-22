package com.hanium.rideornot.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentSearchOuterBinding
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class OuterSearchFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentSearchOuterBinding

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchOuterBinding.inflate(inflater, container, false)
        // inner fragment에서 사라지게 했던 bnv를 다시 활성화함. 조금 원시적인 방법이긴 한데 추후 더 나은 방법이 생각나면 바꾸기
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bnv_main)
        bottomNavigationView.visibility = View.VISIBLE

        binding.clEditTextSearch.setOnClickListener {
            switchToInnerSearchFragment()
        }

        initView()
        setBackBtnHandling()
        return binding.root
    }

    private fun switchToInnerSearchFragment() {
        findNavController().navigate(
            OuterSearchFragmentDirections.actionFragmentOuterSearchToFragmentInnerSearch()
        )
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

}