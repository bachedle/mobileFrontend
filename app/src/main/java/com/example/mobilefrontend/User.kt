package com.example.mobilefrontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class User : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val collectionButton = view.findViewById<TextView>(R.id.btnAddToCollection)
        collectionButton.setOnClickListener {
            findNavController().navigate(R.id.action_users_to_collection)
        }

        return view
    }
}
