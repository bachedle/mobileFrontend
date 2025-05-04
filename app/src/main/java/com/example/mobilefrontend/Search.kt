package com.example.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass

class Search : Fragment() {

    private lateinit var adapter: AdapterClass
    private val dataList = ArrayList<DataClass>()
    private val originalDataList = ArrayList<DataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCardList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterClass(dataList) { selectedCard ->
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

        // Load sample data simulating backend response
        loadSampleData()

        // Search functionality
        val searchEditText = view.findViewById<EditText>(R.id.etSearch)
        val searchButton = view.findViewById<ImageView>(R.id.ivSearch)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            filterResults(query)
        }

        // Camera scan placeholder (simulate scanning a specific card)
        val cameraButton = view.findViewById<ImageView>(R.id.ivCamera)
        cameraButton.setOnClickListener {
            // Simulate a scan result (e.g., looking for "Meowscarada")
            filterResults("Meowscarada")
        }

        return view
    }

    // Function to load sample data (simulating backend data)
    private fun loadSampleData() {
        originalDataList.clear()
        originalDataList.add(DataClass(R.drawable.sample_card, "Meowscarada EX", "151", "Ultra Rare", "151-193"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Pikachu", "Base Set", "Rare", "58-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Charizard", "Base Set", "Rare Holo", "6-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Blastoise", "Base Set", "Rare Holo", "2-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Venusaur", "Base Set", "Rare Holo", "15-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Gengar", "Fossil", "Rare Holo", "5-62"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Dragonite", "Fossil", "Rare Holo", "4-62"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Mewtwo", "Base Set", "Rare Holo", "10-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Meowscarada", "Paldea Evolved", "Rare", "15-279"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Skeledirge EX", "Paldea Evolved", "Ultra Rare", "22-279"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Tinkaton", "Paldea Evolved", "Rare", "83-279"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Arcanine", "Jungle", "Rare", "23-64"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Gyarados", "Base Set", "Rare Holo", "6-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Lugia", "Neo Genesis", "Rare Holo", "9-111"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Umbreon", "Neo Destiny", "Rare Holo", "13-105"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Espeon", "Neo Discovery", "Rare Holo", "1-75"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Pidgeot", "Jungle", "Rare Holo", "8-64"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Zapdos", "Base Set", "Rare Holo", "16-102"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Articuno", "Fossil", "Rare Holo", "2-62"))
        originalDataList.add(DataClass(R.drawable.sample_card, "Moltres", "Fossil", "Rare Holo", "12-62"))
    }

    private fun filterResults(query: String) {
        dataList.clear()
        if (query.isNotEmpty()) {
            for (item in originalDataList) {
                if (item.dataCardName.contains(query, ignoreCase = true)) {
                    dataList.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}