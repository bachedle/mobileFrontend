package com.example.mobilefrontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobilefrontend.viewmodels.UserViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class User : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private var userId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_user, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Collect your user ID once
        viewLifecycleOwner.lifecycleScope.launch {
            userId = userViewModel.userProfileState
                .mapNotNull { it?.data?.id }
                .first()
        }

        // 2) Now set up the click listener
        val collectionButton = view.findViewById<TextView>(R.id.btnAddToCollection)
        collectionButton.setOnClickListener {
            userId?.let { id ->
                val action = UserDirections.actionUsersToCollection(1)
                findNavController().navigate(action)
            } ?: run {
                // ID not ready yet – you might show a toast or disable the button until ready
            }
        }
    }
}
