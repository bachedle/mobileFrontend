package com.example.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>

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

        // Initialize data
        dataList = ArrayList()
        getData()

        // Set adapter with click handler
        recyclerView.adapter = AdapterClass(dataList) { selectedCard ->
            // Navigate to CardDetails fragment using fragment_container
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
    }

    private fun getData() {
        // Sample data (replace with your actual data source, e.g., database or API)
        val imageList = arrayOf(
            R.drawable.sample_card,
            R.drawable.sample_card,
            R.drawable.sample_card
        )
        val cardNameList = arrayOf(
            "Card One",
            "Card Two",
            "Card Three"
        )
        val cardSetList = arrayOf(
            "Set A",
            "Set B",
            "Set C"
        )
        val cardRarityList = arrayOf(
            "Rare",
            "Common",
            "Legendary"
        )
        val cardCodeList = arrayOf(
            "CODE001",
            "CODE002",
            "CODE003"
        )

        // Populate dataList
        for (i in imageList.indices) {
            val dataClass = DataClass(
                imageList[i],
                cardNameList[i],
                cardSetList[i],
                cardRarityList[i],
                cardCodeList[i]
            )
            dataList.add(dataClass)
        }
    }
}