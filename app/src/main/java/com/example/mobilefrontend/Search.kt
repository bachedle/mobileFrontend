package com.example.mobilefrontend

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCardList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterClass(dataList)
        recyclerView.adapter = adapter

        loadSampleData()

        val searchEditText = view.findViewById<EditText>(R.id.etSearch)
        val cameraButton = view.findViewById<ImageView>(R.id.ivCamera)

        // Real-time filtering
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResults(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        cameraButton.setOnClickListener {
            searchEditText.setText("Meowscarada")
        }

        return view
    }

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
            val filtered = originalDataList.filter {
                it.dataCardName.contains(query, ignoreCase = true)
            }
            dataList.addAll(filtered)
        }
        adapter.notifyDataSetChanged()
    }
}
