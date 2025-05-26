package com.example.mobilefrontend

import android.app.Dialog
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mobilefrontend.databinding.FragmentSimulatorBinding
import com.example.mobilefrontend.itemCard.DataClass
import com.yuyakaido.android.cardstackview.*
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorAdapterClass
import com.example.mobilefrontend.itemSimulator.SimulatorCard.SimulatorDataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class Simulator : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!

    private var loadingDialog: Dialog? = null

    private lateinit var cardStackView: CardStackView
    private val cardModel: CardViewModel by viewModel()
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: SimulatorAdapterClass

    private val cardList = ArrayList<SimulatorDataClass>()
    private var series: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigation()
        showLoadingDialog() // Show loading dialog before starting data fetch

        // Return button
        binding.returnBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.returnBtn.bringToFront()

        // Dynamically set margin to account for navbar with minimal buffer
        val navbarHeight = getNavbarHeight()
        val params = binding.pullAnotherButton.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin = navbarHeight + 16
        binding.pullAnotherButton.layoutParams = params

        // Initialize CardStackView
        cardStackView = binding.cardStackView

        // Get series from arguments
        series = SimulatorArgs.fromBundle(requireArguments()).setName

        // Fetch initial cards
        cardModel.getRandomCards(series!!)

        // Set up CardStackLayoutManager
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {
                val currentPosition = manager.topPosition
                if (currentPosition == cardList.size - 1) {
                    // Show Pull Another button when the 9th card is swiped
                    binding.pullAnotherButton.visibility = View.VISIBLE
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {
                // Disable swiping for the 10th card
                if (position == cardList.size - 1) {
                    manager.setCanScrollHorizontal(false)
                    manager.setCanScrollVertical(false)
                } else {
                    manager.setCanScrollHorizontal(true)
                    manager.setCanScrollVertical(true)
                }
            }
            override fun onCardDisappeared(view: View?, position: Int) {}
        })

        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)

        setupCardStack()

        // Pull Another button (resets pulls)
        binding.pullAnotherButton.setOnClickListener {
            showLoadingDialog() // Show loading dialog when resetting pulls
            resetPulls()
        }

        observeCardState()
    }

    private fun getNavbarHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    private fun setupCardStack() {
        adapter = SimulatorAdapterClass(cardList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
    }

    private fun resetPulls() {
        // Clear current cards and fetch new ones
        cardList.clear()
        adapter.notifyDataSetChanged()
        manager.topPosition = 0 // Reset to the first card
        binding.pullAnotherButton.visibility = View.GONE
        cardModel.getRandomCards(series!!)
        // Ensure swiping is enabled for the new set
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
    }

    private fun observeCardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.randomCardsState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data?.filterNotNull() ?: emptyList()
                            cardList.clear()
                            cardList.addAll(cards.map {
                                SimulatorDataClass(
                                    it.id,
                                    it.image_url
                                )
                            })
                            adapter.notifyDataSetChanged()
                            // Ensure swiping is enabled for new cards
                            manager.setCanScrollHorizontal(true)
                            manager.setCanScrollVertical(true)
                            hideLoadingDialog() // Hide loading dialog on success
                        }
                        is ApiResult.Loading -> {
                            // Already shown in onViewCreated or resetPulls
                        }
                        is ApiResult.Error -> {
                            Log.e("Simulator", "Error: ${result.message}")
                            hideLoadingDialog() // Hide loading dialog on error
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun hideBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun showLoadingDialog() {
        if (loadingDialog?.isShowing == true) return

        loadingDialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialog?.setContentView(view)
        loadingDialog?.setCancelable(false)
        loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val loadingImage = view.findViewById<ImageView>(R.id.loading_image)
        val animationDrawable = loadingImage.drawable as? AnimationDrawable
        animationDrawable?.start()

        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation()
        hideLoadingDialog() // Ensure dialog is dismissed when leaving the fragment
        _binding = null
    }
}