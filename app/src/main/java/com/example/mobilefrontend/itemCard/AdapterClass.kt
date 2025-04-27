package com.example.mobilefrontend.itemCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefrontend.R

class AdapterClass(private val dataList: ArrayList<DataClass>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvImage.setImageResource(currentItem.dataImage)
        holder.rvCardName.text = currentItem.dataCardName
        holder.rvCardSet.text = currentItem.dataCardSet
        holder.rvCardRarity.text = currentItem.dataCardRarity
        holder.rvCardCode.text = currentItem.dataCardCode
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvImage:ImageView = itemView.findViewById(R.id.imgCard)

        val rvCardName:TextView = itemView.findViewById(R.id.tvCardName)
        val rvCardSet:TextView = itemView.findViewById(R.id.tvCardSet)
        val rvCardRarity:TextView = itemView.findViewById(R.id.tvCardRarity)
        val rvCardCode:TextView = itemView.findViewById(R.id.tvCardCode)
    }
}