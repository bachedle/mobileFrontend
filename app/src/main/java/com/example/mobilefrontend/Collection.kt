package com.example.mobilefrontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.launch

class Collection : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterClass

    private val cardModel: CardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = AdapterClass(arrayListOf()) { selectedCard ->
//            val action = HomeDirections.actionHomeToCardDetail(
//                selectedCard.dataImage,
//                selectedCard.dataCardName,
//                selectedCard.dataCardSet,
//                selectedCard.dataCardRarity,
//                selectedCard.dataCardCode
//            )
//            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        observeCardState()
        cardModel.getCards()

        return view
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.cardState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data ?: emptyList()
                            adapter.updateData(cards.map {
                                DataClass(
                                    it.image_url,
                                    it.name,
                                    "Paldea Evolved", // Replace with real values if available
                                    it.rarity,
                                    it.code
                                )
                            })
                        }
                        is ApiResult.Loading -> {
                            // Optionally show a loading indicator
                        }
                        is ApiResult.Error -> {
                            Log.e("Collection", "Error: ${result.message}")
                        }
                        null -> {
                            // No-op
                        }
                    }
                }
            }
        }
    }
}
