package com.example.mobilefrontend.itemCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilefrontend.R

class AdapterClass(
    private val dataList: ArrayList<DataClass>,
    private val isGrid: Boolean = false,
    private val onItemClick: (DataClass) -> Unit
) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        // Configure the image scale type based on the mode.
        if (isGrid) {
            // Using FIT_CENTER (or CENTER_INSIDE) ensures the entire image is shown.
            holder.rvImage.scaleType = ImageView.ScaleType.FIT_CENTER
        } else {
            holder.rvImage.scaleType = ImageView.ScaleType.FIT_XY
        }

        // Load image using Glide.
        Glide.with(holder.itemView.context)
            .load(currentItem.dataImage)
            .placeholder(R.drawable.samplecard)
            .error(R.drawable.samplecard)
            .into(holder.rvImage)

        holder.rvCardName.text = currentItem.dataCardName
        holder.rvCardSet.text = currentItem.dataCardSet
        holder.rvCardRarity.text = currentItem.dataCardRarity
        holder.rvCardCode.text = currentItem.dataCardCode

        // In grid mode, adjust constraints so that the image sits on top with a fixed height,
        // and the text views are positioned below it.
        if (isGrid) {
            val rootLayout = holder.rootLayout
            val constraintSet = ConstraintSet()
            constraintSet.clone(rootLayout)

            // For the image:
            // Clear the extra constraints and attach it to parent's top, start, and end.
            constraintSet.clear(R.id.imgCard, ConstraintSet.START)
            constraintSet.clear(R.id.imgCard, ConstraintSet.END)
            constraintSet.clear(R.id.imgCard, ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.imgCard, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8)
            constraintSet.connect(R.id.imgCard, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 8)
            constraintSet.connect(R.id.imgCard, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)
            // Instead of setting a ratio, set a fixed height (from dimens) and let the width match.
            val fixedHeightPx = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.card_image_height)
            constraintSet.constrainWidth(R.id.imgCard, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainHeight(R.id.imgCard, fixedHeightPx)

            // For tvCardName:
            constraintSet.clear(R.id.tvCardName, ConstraintSet.START)
            constraintSet.clear(R.id.tvCardName, ConstraintSet.TOP)
            constraintSet.clear(R.id.tvCardName, ConstraintSet.END)
            constraintSet.connect(R.id.tvCardName, ConstraintSet.TOP, R.id.imgCard, ConstraintSet.BOTTOM, 8)
            constraintSet.connect(R.id.tvCardName, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 8)
            constraintSet.connect(R.id.tvCardName, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)
            constraintSet.setHorizontalBias(R.id.tvCardName, 0.5f)

            // For tvCardSet:
            constraintSet.clear(R.id.tvCardSet, ConstraintSet.START)
            constraintSet.clear(R.id.tvCardSet, ConstraintSet.TOP)
            constraintSet.clear(R.id.tvCardSet, ConstraintSet.END)
            constraintSet.connect(R.id.tvCardSet, ConstraintSet.TOP, R.id.tvCardName, ConstraintSet.BOTTOM, 4)
            constraintSet.connect(R.id.tvCardSet, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 8)
            constraintSet.connect(R.id.tvCardSet, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)

            // For tvCardRarity:
            constraintSet.clear(R.id.tvCardRarity, ConstraintSet.START)
            constraintSet.clear(R.id.tvCardRarity, ConstraintSet.TOP)
            constraintSet.clear(R.id.tvCardRarity, ConstraintSet.END)
            constraintSet.connect(R.id.tvCardRarity, ConstraintSet.TOP, R.id.tvCardSet, ConstraintSet.BOTTOM, 4)
            constraintSet.connect(R.id.tvCardRarity, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 8)
            constraintSet.connect(R.id.tvCardRarity, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)

            // For tvCardCode:
            constraintSet.clear(R.id.tvCardCode, ConstraintSet.START)
            constraintSet.clear(R.id.tvCardCode, ConstraintSet.TOP)
            constraintSet.clear(R.id.tvCardCode, ConstraintSet.END)
            constraintSet.connect(R.id.tvCardCode, ConstraintSet.TOP, R.id.tvCardRarity, ConstraintSet.BOTTOM, 4)
            constraintSet.connect(R.id.tvCardCode, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 8)
            constraintSet.connect(R.id.tvCardCode, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)

            constraintSet.applyTo(rootLayout)
        }

        // Set up the click listener.
        holder.itemView.setOnClickListener { onItemClick(currentItem) }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<DataClass>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)
        val rvImage: ImageView = itemView.findViewById(R.id.imgCard)
        val rvCardName: TextView = itemView.findViewById(R.id.tvCardName)
        val rvCardSet: TextView = itemView.findViewById(R.id.tvCardSet)
        val rvCardRarity: TextView = itemView.findViewById(R.id.tvCardRarity)
        val rvCardCode: TextView = itemView.findViewById(R.id.tvCardCode)
    }
}
