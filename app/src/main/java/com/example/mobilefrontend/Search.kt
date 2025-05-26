package com.example.mobilefrontend

import android.app.Dialog // 🔧 added
import android.graphics.drawable.AnimationDrawable // 🔧 added
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // 🔧 added
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilefrontend.databinding.FragmentSearchBinding
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class Search : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterClass
    private val dataList = ArrayList<DataClass>()
    private val originalDataList = ArrayList<DataClass>()
    private val cardModel: CardViewModel by viewModel()

    // 🔧 Loading dialog reference
    private var loadingDialog: Dialog? = null

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

        // Return button
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Observe backend results
        observeCardState()

        // Search
        binding.ivSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            cardModel.getCards(keyword = query)
        }

        // Navigate to camera
        binding.ivCamera.setOnClickListener {
            val action = SearchDirections.actionSearchToImageSearch()
            findNavController().navigate(action)
        }
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.cardState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            hideLoadingDialog() // 🔧 hide loading
                            val cards = result.data ?: emptyList()

                            val mappedCards = cards.map {
                                DataClass(
                                    it.id,
                                    it.image_url,
                                    it.name,
                                    "Paldea Evolved", // Ideally use real set name
                                    it.rarity,
                                    it.code
                                )
                            }

                            originalDataList.clear()
                            originalDataList.addAll(mappedCards)

                            dataList.clear()
                            dataList.addAll(originalDataList)

                            adapter.notifyDataSetChanged()
                        }

                        is ApiResult.Loading -> {
                            showLoadingDialog() // 🔧 show loading
                        }

                        is ApiResult.Error -> {
                            hideLoadingDialog() // 🔧 hide loading
                            Log.e("SearchFragment", "Error: ${result.message}")
                        }

                        null -> Unit
                    }
                }
            }
        }
    }

    // 🔧 Show loading dialog
    private fun showLoadingDialog() {
        if (loadingDialog?.isShowing == true) return

        loadingDialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialog?.setContentView(view)
        loadingDialog?.setCancelable(false)
        loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val loadingImage = view.findViewById<ImageView>(R.id.loading_image)
        val animationDrawable = loadingImage.drawable as? AnimationDrawable
        animationDrawable?.start()

        loadingDialog?.show()
    }

    // 🔧 Hide loading dialog
    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideLoadingDialog() // 🔧 dismiss loading on cleanup
        _binding = null
    }
}
