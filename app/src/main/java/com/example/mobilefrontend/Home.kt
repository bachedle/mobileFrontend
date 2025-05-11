package com.example.mobilefrontend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.launch

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView

    //them cai nay
    private lateinit var adapter: AdapterClass
    private val cardModel: CardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val moveToSearchFragment : TextView = view.findViewById(R.id.nav_search)
//        moveToSearchFragment.setOnClickListener {
//            findNavController().navigate(R.id.action_home_to_search)
//        }
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)


        // Set adapter with click handler
        adapter = AdapterClass(arrayListOf()) { selectedCard ->
            val cardDetailsFragment = CardDetails.newInstance(
                selectedCard.dataImage,
                selectedCard.dataCardName,
                selectedCard.dataCardSet,
                selectedCard.dataCardRarity,
                selectedCard.dataCardCode
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, cardDetailsFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        observeCardState()
        cardModel.getCards()

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
                                    "Paldea Evolved",  // replace with actual fields
                                    it.rarity,
                                    it.code
                                )
                            })
                        }
                        is ApiResult.Loading -> {
                            // Show loading UI (optional)
                        }
                        is ApiResult.Error -> {
                            Log.e("Home", "Error: ${result.message}")
                        }
                        null -> {}
                    }
                }
            }
        }
    }

}