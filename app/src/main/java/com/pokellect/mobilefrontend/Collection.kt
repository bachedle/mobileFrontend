package com.pokellect.mobilefrontend

import android.app.Dialog
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pokellect.mobilefrontend.itemCard.AdapterClass
import com.pokellect.mobilefrontend.itemCard.DataClass
import com.pokellect.mobilefrontend.repository.ApiResult
import com.pokellect.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.launch

class Collection : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterClass

    private var loadingDialog: Dialog? = null

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

        // Return button
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }

        observeCardState()
        cardModel.getUserCollection(userId = userId)

        return view
    }

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

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.userCollectionState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            hideLoadingDialog() // Hide dialog on success
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
                            showLoadingDialog() // Show dialog when loading
                        }
                        is ApiResult.Error -> {
                            hideLoadingDialog() // Hide dialog on error
                            Log.e("Collection", "Error: ${result.message}")
                        }
                        null -> {
                            hideLoadingDialog() // Hide dialog for null state
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideLoadingDialog() // Ensure dialog is dismissed when view is destroyed
    }
}