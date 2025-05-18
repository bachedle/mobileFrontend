package com.example.mobilefrontend.itemSimulator.SimulatorSet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.databinding.PackLayoutBinding

class CardSetAdapter(
    private val cardSets: List<SetDataClass>,
    private val onClick: (SetDataClass) -> Unit
) : RecyclerView.Adapter<CardSetAdapter.CardSetViewHolder>() {

    var currentCenteredPosition = 0

    inner class CardSetViewHolder(val binding: PackLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardSetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PackLayoutBinding.inflate(inflater, parent, false)
        return CardSetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardSetViewHolder, position: Int) {
        val actualPosition = position % cardSets.size
        val item = cardSets[actualPosition]

        holder.binding.packImage.setImageResource(item.imageResId)

        // Scale for center effect
        holder.itemView.scaleX = if (position == currentCenteredPosition) 1.2f else 1f
        holder.itemView.scaleY = if (position == currentCenteredPosition) 1.2f else 1f

        holder.itemView.setOnClickListener {
            if (position == currentCenteredPosition) {
                onClick(item)
            }
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}
