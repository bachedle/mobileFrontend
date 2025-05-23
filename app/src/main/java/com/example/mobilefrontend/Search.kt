package com.example.mobilefrontend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.databinding.FragmentLoginBinding
import com.example.mobilefrontend.databinding.FragmentSearchBinding
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class Search : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterClass
    private val dataList = ArrayList<DataClass>()
    private val originalDataList = ArrayList<DataClass>()
    private val cardModel: CardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.rvCardList.layoutManager = LinearLayoutManager(context)
        binding.rvCardList.setHasFixedSize(true)

        adapter = AdapterClass(dataList) { selectedCard ->
            val action = SearchDirections.actionSearchToCardDetail(
                selectedCard.dataId,
                selectedCard.dataImage,
                selectedCard.dataCardName,
                selectedCard.dataCardSet,
                selectedCard.dataCardRarity,
                selectedCard.dataCardCode,
                false
            )
            findNavController().navigate(action)
        }
        binding.rvCardList.adapter = adapter

        //nut return
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Load sample data simulating backend response
        observeCardState()

        // Search functionality
        binding.ivSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            cardModel.getCards(keyword = query)
        }

        // Example camera button if needed
//        binding.ivCamera.setOnClickListener {
//            cardModel.getCards(keyword = "Meowscarada")
//        }
    }


    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.cardState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data ?: emptyList()

                            // Convert API models to DataClass
                            val mappedCards = cards.map {
                                DataClass(
                                    it.id,
                                    it.image_url,
                                    it.name,
                                    "Paldea Evolved", // Or use real set name if available
                                    it.rarity,
                                    it.code
                                )
                            }
                            // Populate original list for filtering
                            originalDataList.clear()
                            originalDataList.addAll(mappedCards)

                            // Copy all to dataList (visible list)
                            dataList.clear()
                            dataList.addAll(originalDataList)

                            adapter.notifyDataSetChanged()
                        }

                        is ApiResult.Loading -> {
                            // Optional: Show loading spinner
                        }

                        is ApiResult.Error -> {
                            Log.e("SearchFragment", "Error: ${result.message}")
                        }

                        null -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}