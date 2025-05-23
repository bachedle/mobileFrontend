package com.example.mobilefrontend.itemSimulator.SimulatorCard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilefrontend.R
import com.example.mobilefrontend.databinding.SimulatorLayoutBinding
import com.example.mobilefrontend.itemCard.DataClass


class SimulatorAdapterClass(
    private val cardList: ArrayList<SimulatorDataClass>) :
    RecyclerView.Adapter<SimulatorAdapterClass.CardViewHolder>() {

    inner class CardViewHolder(val binding: SimulatorLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SimulatorLayoutBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        // Load image using Glide with the resource ID from our data class.
        Glide.with(holder.itemView.context)
            .load(card.image_url)
            .placeholder(R.drawable.samplecard)  // optional fallback image
            .error(R.drawable.samplecard)        // optional error image
            .into(holder.binding.cardImage)           // ensure ivCard matches your ImageView ID in the layout
    }

    override fun getItemCount(): Int = cardList.size

    fun updateData(newData: List<SimulatorDataClass>) {
        cardList.clear()
        cardList.addAll(newData)
        notifyDataSetChanged()
    }
}
