package com.example.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class CardDetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carddetail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the individual fields from arguments
        val dataImage = arguments?.getInt("dataImage") ?: 0
        val dataCardName = arguments?.getString("dataCardName") ?: ""
        val dataCardSet = arguments?.getString("dataCardSet") ?: ""
        val dataCardRarity = arguments?.getString("dataCardRarity") ?: ""
        val dataCardCode = arguments?.getString("dataCardCode") ?: ""

        // Populate the UI with card details
        view.findViewById<ImageView>(R.id.ivCardImage).setImageResource(dataImage)
        view.findViewById<TextView>(R.id.tvCardName).text = dataCardName
        view.findViewById<TextView>(R.id.tvRarity).text = dataCardRarity
        view.findViewById<TextView>(R.id.tvSet).text = dataCardSet
        view.findViewById<TextView>(R.id.tvCode).text = dataCardCode

        // Add to Collection button functionality (placeholder)
        val addToCollectionButton = view.findViewById<Button>(R.id.btnAddToCollection)
        addToCollectionButton.setOnClickListener {
            Toast.makeText(context, "Added to Collection: $dataCardName", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual collection logic here
        }

        // Ensure content is not hidden behind bottom navigation
        val rootLayout = view.findViewById<LinearLayout>(R.id.llCardDetailsRoot)
        val extraPadding = (resources.displayMetrics.density * 100).toInt()
        rootLayout.setPadding(
            rootLayout.paddingLeft,
            rootLayout.paddingTop,
            rootLayout.paddingRight,
            extraPadding
        )
    }

    companion object {
        fun newInstance(
            dataImage: Int,
            dataCardName: String,
            dataCardSet: String,
            dataCardRarity: String,
            dataCardCode: String
        ): CardDetails {
            val fragment = CardDetails()
            val args = Bundle()
            args.putInt("dataImage", dataImage)
            args.putString("dataCardName", dataCardName)
            args.putString("dataCardSet", dataCardSet)
            args.putString("dataCardRarity", dataCardRarity)
            args.putString("dataCardCode", dataCardCode)
            fragment.arguments = args
            return fragment
        }
    }
}
