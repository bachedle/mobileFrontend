package com.example.mobilefrontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        // Retrieve the individual fields from arguments
        val args = CollectionArgs.fromBundle(requireArguments())

        val userId = args.userId

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = AdapterClass(arrayListOf(), isGrid = true) { selectedCard ->
            val action = CollectionDirections.actionCollectionToCardDetail(
                selectedCard.dataId,
                selectedCard.dataImage,
                selectedCard.dataCardName,
                selectedCard.dataCardSet,
                selectedCard.dataCardRarity,
                selectedCard.dataCardCode,
                true
            )
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        //nut return
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }

        observeCardState()
        cardModel.getUserCollection(userId = userId)

        return view
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.userCollectionState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data ?: emptyList()
                            adapter.updateData(cards.map {
                                DataClass(
                                    it.card.id,
                                    it.card.image_url,
                                    it.card.name,
                                    "Paldea Evolved", // Replace with real values if available
                                    it.card.rarity,
                                    it.card.code
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
