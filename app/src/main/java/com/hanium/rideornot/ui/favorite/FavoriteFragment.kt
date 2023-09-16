package com.hanium.rideornot.ui.favorite

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanium.rideornot.R
import com.hanium.rideornot.databinding.FragmentFavoriteBinding
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.ui.common.ViewModelFactory
import com.hanium.rideornot.ui.search.SearchViewModel

class FavoriteFragment : Fragment(), IFavoriteRV, IFavoriteEditRV {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var favoriteRVAdapter: FavoriteRVAdapter
    private lateinit var favoriteEditRVAdapter: FavoriteEditRVAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val favoriteViewModel: FavoriteViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val searchViewModel: SearchViewModel by viewModels { ViewModelFactory(requireContext()) }

    private var isEditing = false
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
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        setBackBtnHandling()

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvFavorite.setHasFixedSize(true)

        binding.llEditBtn.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                binding.ivEditIcon.setImageResource(R.drawable.ic_check_circle_24)
                binding.tvEdit.text = getString(R.string.favorite_edit_apply)
                itemTouchHelper.attachToRecyclerView(binding.rvFavorite)
                binding.rvFavorite.adapter = favoriteEditRVAdapter
            } else {
                isEditing = false
                binding.ivEditIcon.setImageResource(R.drawable.ic_edit_24)
                binding.tvEdit.text = getString(R.string.favorite_edit_start)
                itemTouchHelper.attachToRecyclerView(null)
                binding.rvFavorite.adapter = favoriteRVAdapter
                applyChangesToDatabase()
            }
        }

        favoriteViewModel.favoriteList.observe(this) {
            favoriteRVAdapter = FavoriteRVAdapter(
                requireContext(),
                // 즐겨찾기 목록을 order에 따라 정렬하여 어댑터에 전달
                favoriteViewModel.favoriteList.value!!.sortedBy { it.orderNumber },
                favoriteViewModel,
                searchViewModel,
                this
            )
            val mutableFavoriteList = favoriteViewModel.favoriteList.value!!.sortedBy { it.orderNumber }.toMutableList()
            favoriteEditRVAdapter = FavoriteEditRVAdapter(
                requireContext(),
                mutableFavoriteList,
                searchViewModel
            )
            val favoriteItemTouchHelperCallback = FavoriteItemTouchHelperCallback(favoriteEditRVAdapter).apply {
                setClamp(resources.displayMetrics.widthPixels.toFloat() / 8)
            }
            itemTouchHelper = ItemTouchHelper(favoriteItemTouchHelperCallback)
            binding.rvFavorite.adapter = favoriteRVAdapter
            binding.rvFavorite.setOnTouchListener { _, _ ->
                favoriteItemTouchHelperCallback.removePreviousClamp(binding.rvFavorite)
                false
            }

            if (favoriteViewModel.favoriteList.value!!.size == 0) {
                binding.tvNoFavoriteExists.visibility = View.VISIBLE
            } else {
                binding.tvNoFavoriteExists.visibility = View.GONE
            }
        }


        return binding.root
    }

    private fun applyChangesToDatabase() {
        val updatedList = favoriteEditRVAdapter.itemList
        favoriteViewModel.updateOrder(updatedList)
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

    override fun onDeleteBtnClick(favorite: Favorite) {
        favoriteViewModel.deleteFavorite(favorite)
    }

}