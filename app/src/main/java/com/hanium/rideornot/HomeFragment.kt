package com.hanium.rideornot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.hanium.rideornot.databinding.FragmentHomeBinding
import com.hanium.rideornot.gps.GpsManager

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationTestButton: Button? = view.findViewById(R.id.locationTestButton)
        locationTestButton?.setOnClickListener {

        }

        val startLocationUpdateButton: Button? = view.findViewById(R.id.startLocationUpdateButton)
        startLocationUpdateButton?.setOnClickListener {
            GpsManager.startLocationUpdates()
        }

        val stopLocationUpdateButton: Button? = view.findViewById(R.id.stopLocationUpdateButton)
        stopLocationUpdateButton?.setOnClickListener {
            GpsManager.stopLocationUpdates()

        }

    }

}