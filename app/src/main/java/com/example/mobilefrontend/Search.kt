package com.example.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
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
        recyclerView.adapter = adapter

        //nut return
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }

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

         val imgURL = "https://res.cloudinary.com/tcg-card/image/upload/v1746548420/cards/Quaquaval.png"

        originalDataList.clear()
        originalDataList.add(DataClass(1, imgURL, "Meowscarada EX", "151", "Ultra Rare", "151-193"))
        originalDataList.add(DataClass(2, imgURL, "Pikachu", "Base Set", "Rare", "58-102"))
        originalDataList.add(DataClass(3, imgURL, "Charizard", "Base Set", "Rare Holo", "6-102"))
        originalDataList.add(DataClass(4, imgURL, "Blastoise", "Base Set", "Rare Holo", "2-102"))
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