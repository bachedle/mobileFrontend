package com.example.mobilefrontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobilefrontend.databinding.FragmentSimulatorBinding
import com.yuyakaido.android.cardstackview.*
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorAdapterClass
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorDataClass

class Simulator : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: SimulatorAdapterClass

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

    }

    private fun setupCardStack() {
        val cardList = (1..10).map { index ->
            SimulatorDataClass(
                imageResId = R.drawable.samplecard2
            )
        }
        adapter = SimulatorAdapterClass(cardList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
