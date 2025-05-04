package com.example.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.mobilefrontend.itemCard.DataClass

class CardDetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_carddetail, container, false)

        // Retrieve the selected card data from arguments
        val card = arguments?.getParcelable<DataClass>("selectedCard")

        // Populate the UI with card details
        if (card != null) {
            view.findViewById<ImageView>(R.id.ivCardImage).setImageResource(card.dataImage)
            view.findViewById<TextView>(R.id.tvCardName).text = card.dataCardName
            view.findViewById<TextView>(R.id.tvRarity).text = card.dataCardRarity
            view.findViewById<TextView>(R.id.tvSet).text = card.dataCardSet
        }

        // Add to Collection button functionality (placeholder)
        val addToCollectionButton = view.findViewById<Button>(R.id.btnAddToCollection)
        addToCollectionButton.setOnClickListener {
            Toast.makeText(context, "Added to Collection: ${card?.dataCardName}", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual collection logic here
        }

        return view
    }

    companion object {
        fun newInstance(card: DataClass): CardDetails {
            val fragment = CardDetails()
            val args = Bundle()
            args.putParcelable("selectedCard", card)
            fragment.arguments = args
            return fragment
        }
    }
}