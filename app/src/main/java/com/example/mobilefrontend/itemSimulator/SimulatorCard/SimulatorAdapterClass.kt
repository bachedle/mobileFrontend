package com.example.mobilefrontend.itemSimulator.SimulatorCard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.databinding.SimulatorLayoutBinding
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorDataClass

class SimulatorAdapterClass(private val cardList: List<SimulatorDataClass>) : RecyclerView.Adapter<SimulatorAdapterClass.CardViewHolder>() {

    inner class CardViewHolder(val binding: SimulatorLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SimulatorLayoutBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.binding.cardImage.setImageResource(card.imageResId)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}