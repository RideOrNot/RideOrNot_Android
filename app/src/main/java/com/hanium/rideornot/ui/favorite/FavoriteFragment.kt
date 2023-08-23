package com.hanium.rideornot.ui.favorite

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentFavoriteBinding
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.ui.SearchViewModel
import com.hanium.rideornot.ui.search.InnerSearchFragmentDirections
import io.reactivex.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(), IFavoriteRV {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var favoriteRVAdapter: FavoriteRVAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var searchViewModel: SearchViewModel

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

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        setBackBtnHandling()

        favoriteViewModel = FavoriteViewModel(requireContext())
        searchViewModel = SearchViewModel(requireContext())

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvFavorite.setHasFixedSize(true)
        favoriteViewModel.favoriteList.observe(this) {
            favoriteRVAdapter = FavoriteRVAdapter(
                requireContext(),
                favoriteViewModel.favoriteList.value!!,
                favoriteViewModel,
                searchViewModel,
                this
            )
            binding.rvFavorite.adapter = favoriteRVAdapter
            favoriteRVAdapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

//    fun handleFavoriteAddition(favorite: Favorite) {
//        favoriteViewModel.insertFavorite(favorite)
//    }
//
//    fun handleFavoriteDeletion(favorite: Favorite) {
//        favoriteViewModel.deleteFavorite(favorite)
//    }

    override fun onFavoriteItemClick(favorite: Favorite) {
        switchToStationDetailFragment(favorite.stationName)
    }

    private fun switchToStationDetailFragment(stationName: String) {
        findNavController().navigate(
            FavoriteFragmentDirections.actionFragmentFavoriteToActivityStationDetail(stationName)
        )
    }

}