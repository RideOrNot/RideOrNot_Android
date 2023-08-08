package com.hanium.rideornot.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hanium.rideornot.databinding.FragmentFavoriteBinding

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