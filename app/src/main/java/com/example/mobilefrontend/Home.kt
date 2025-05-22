package com.example.mobilefrontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import com.example.mobilefrontend.viewmodels.UserViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: AdapterClass
    private val cardModel: CardViewModel by viewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)


        // Set adapter with click handler
        adapter = AdapterClass(arrayListOf()) { selectedCard ->
            val action = HomeDirections.actionHomeToCardDetail(
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


        //add nut de nhay qua collection o trang home
        val collectionButton = view.findViewById<Button>(R.id.collectionTextBtn)

        collectionButton.setOnClickListener {
            //thay id trong action cc gi do
            findNavController().navigate(HomeDirections.actionHomeToCollection(3))
        }

        observeCardState()

        viewLifecycleOwner.lifecycleScope.launch {
            // suspend until we get a non-null ID, then cancel
            val id = userViewModel.userProfileState
                .mapNotNull { it?.data?.id }
                .first()
            cardModel.getUserCollection(id)
        }
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.userCollectionState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data ?: emptyList()
                            adapter.updateData(cards.takeLast(3).map {
                                DataClass(
                                    it.card.id,
                                    it.card.image_url,
                                    it.card.name,
                                    "Paldea Evolved",
                                    it.card.rarity,
                                    it.card.code
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