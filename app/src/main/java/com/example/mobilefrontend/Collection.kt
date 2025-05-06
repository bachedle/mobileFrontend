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

        // Sample card data using image URLs
        cardList = ArrayList()
        repeat(8) {
            cardList.add(
                DataClass(
                    "https://images.squarespace-cdn.com/content/v1/5cf4cfa4382ac0000123aa1b/1686247761991-VBAA2APE3IMJUAPZI9Y4/sv2_en_164.png?format=300w", // Replace with real image URL
                    "Meowscarada EX",
                    "Paldea Evolved",
                    "Special Illustration Rare",
                    "256/193"
                )
            )
        }

        adapter = AdapterClass(cardList) { selectedCard ->
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
