package com.pokellect.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pokellect.mobilefrontend.databinding.FragmentCarddetailBinding
import com.pokellect.mobilefrontend.model.AddCardToCollectionRequest
import com.pokellect.mobilefrontend.repository.ApiResult
import com.pokellect.mobilefrontend.viewmodels.CardViewModel
import com.pokellect.mobilefrontend.viewmodels.UserViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class CardDetails : Fragment() {
    private var _binding: FragmentCarddetailBinding? = null
    private val binding get() = _binding!!
    private val cardModel: CardViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var userId: Int? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarddetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the individual fields from arguments
        val args = CardDetailsArgs.fromBundle(requireArguments())

        val dataId = args.dataId
        val dataImage = args.dataImage
        val dataCardName = args.dataCardName
        val dataCardSet = args.dataCardSet
        val dataCardRarity = args.dataCardRarity
        val dataCardCode = args.dataCardCode
        val existInCollection = args.existInCollection

        // Use Glide to load the image URL into ImageView
        val imageView = view.findViewById<ImageView>(R.id.ivCardImage)
        if (dataImage.isNotEmpty()) {
            Glide.with(requireContext())
                .load(dataImage)
                .placeholder(R.drawable.samplecard) // Add a placeholder image
                .error(R.drawable.samplecard)  // Add an error image
                .into(imageView)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            userId = userViewModel.userProfileState
                .mapNotNull { it?.data?.id }
                .first()
        }
        // Populate the UI with card details
        binding.tvCardName.text = dataCardName
        binding.tvRarity.text = dataCardRarity
        binding.tvSet.text = dataCardSet
        binding.tvCode.text = dataCardCode

        if (existInCollection) {
            binding.btnAddToCollection.visibility = View.GONE
        }

        // Add to Collection
        binding.btnAddToCollection.setOnClickListener {
            if(userId != null) {
                val payload = AddCardToCollectionRequest(user_id = userId!!, card_id = dataId)
                cardModel.addToCollection(payload)
                it.isEnabled = false
            }

        }

        //nut return
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }


        // Observe result and navigate or re-enable button
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.collectionState
                    .filterNotNull()
                    .collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Added to Collection: $dataCardName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is ApiResult.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to add: ${result.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is ApiResult.Loading -> {
                                // Optionally show loading state
                            }
                        }
                        cardModel.resetCollectionState()
                        binding.btnAddToCollection.isEnabled = true
                    }
            }
        }

        // Prevent bottom nav overlap (if needed)
        binding.llCardDetailsRoot.setPadding(
            binding.llCardDetailsRoot.paddingLeft,
            binding.llCardDetailsRoot.paddingTop,
            binding.llCardDetailsRoot.paddingRight,
            (resources.displayMetrics.density * 100).toInt()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
