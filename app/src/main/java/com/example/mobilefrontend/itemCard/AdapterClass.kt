package com.example.mobilefrontend.itemCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilefrontend.R

class AdapterClass(
    private val dataList: ArrayList<DataClass>,
    private val onItemClick: (DataClass) -> Unit
) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        // Load image from URL using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.dataImage)
            .placeholder(R.drawable.sample_card) // optional fallback image
            .error(R.drawable.sample_card) // optional error image
            .into(holder.rvImage)

        holder.rvCardName.text = currentItem.dataCardName
        holder.rvCardSet.text = currentItem.dataCardSet
        holder.rvCardRarity.text = currentItem.dataCardRarity
        holder.rvCardCode.text = currentItem.dataCardCode

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<DataClass>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.imgCard)
        val rvCardName: TextView = itemView.findViewById(R.id.tvCardName)
        val rvCardSet: TextView = itemView.findViewById(R.id.tvCardSet)
        val rvCardRarity: TextView = itemView.findViewById(R.id.tvCardRarity)
        val rvCardCode: TextView = itemView.findViewById(R.id.tvCardCode)
    }
}
