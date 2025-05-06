package com.example.mobilefrontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.R

class Collection : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterClass
    private lateinit var cardList: ArrayList<DataClass>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Sample data
        cardList = ArrayList()
        repeat(8) {
            cardList.add(
                DataClass(
                    R.drawable.sample_card,
                    "Meowscarada EX",
                    "Paldea Evolved",
                    "Special Illustration Rare",
                    "256/193"
                )
            )
        }

        adapter = AdapterClass(cardList) { selectedCard ->
            // Navigate to CardDetails fragment when a card is clicked
            val cardDetailsFragment = CardDetails.newInstance(
                selectedCard.dataImage,
                selectedCard.dataCardName,
                selectedCard.dataCardSet,
                selectedCard.dataCardRarity,
                selectedCard.dataCardCode
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, cardDetailsFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        return view
    }
}