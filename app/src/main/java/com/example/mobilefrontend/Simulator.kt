package com.example.mobilefrontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mobilefrontend.databinding.FragmentSimulatorBinding
import com.example.mobilefrontend.itemCard.DataClass
import com.yuyakaido.android.cardstackview.*
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorAdapterClass
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorDataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class Simulator : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!


    private lateinit var cardStackView: CardStackView
    private val cardModel: CardViewModel by viewModel()
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: SimulatorAdapterClass

    private val cardList = ArrayList<SimulatorDataClass>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //nut return
        binding.returnBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.returnBtn.bringToFront()

        cardStackView = binding.cardStackView



        val args = SimulatorArgs.fromBundle(requireArguments())
        val series = args.setName

        cardModel.getRandomCards(series)



        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}
            override fun onCardSwiped(direction: Direction?) {
                when (direction) {
                    Direction.Right -> { /* Right swipe action */ }
                    Direction.Left -> { /* Left swipe action */ }
                    else -> {}
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })



        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        setupCardStack()


        observeCardState()


    }

    private fun setupCardStack() {
//        val cardList = (1..10).map { index ->
//            SimulatorDataClass(
//                imageResId = "https://images.squarespace-cdn.com/content/v1/5cf4cfa4382ac0000123aa1b/1743107665171-T2ILLD1K87G4LH2IE4KL/SV09_EN_4-2x.png?format=300w"
//            )
//        }




        adapter = SimulatorAdapterClass(cardList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.randomCardsState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data?.filterNotNull()?: emptyList()
                            adapter.updateData(cards.map {
                                SimulatorDataClass(
                                    it.id,
                                    it.image_url,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
