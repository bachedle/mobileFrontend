package com.example.mobilefrontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mobilefrontend.databinding.FragmentSimulatorMainBinding
import com.example.mobilefrontend.itemSimulator.SimulatorSet.CardSetAdapter
import com.example.mobilefrontend.itemSimulator.SimulatorSet.SetDataClass


class SimulatorMain : Fragment() {
    private lateinit var binding: FragmentSimulatorMainBinding
    private lateinit var adapter: CardSetAdapter

    private val cardSets = listOf(
        SetDataClass("Journey Together", R.drawable.journey_together),
        SetDataClass("Paldea Evolved", R.drawable.paldea_evolved),
        SetDataClass("Surging Sparks", R.drawable.twilight_masquerade),
        SetDataClass("Prismatic Evolution", R.drawable.prismatic_evolution)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSimulatorMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CardSetAdapter(cardSets) { selectedSet ->
            val action = SimulatorMainDirections.actionSimulatorMainToSimulator(selectedSet.name)
            findNavController().navigate(action)
        }

        val viewPager = binding.viewPager
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.offscreenPageLimit = 3

        viewPager.setPageTransformer { page, position ->
            val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
            page.scaleX = scale
            page.scaleY = scale
        }

        //nut return
        val returnButton = view.findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewPager.setCurrentItem(Int.MAX_VALUE / 2, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                adapter.currentCenteredPosition = position
                adapter.notifyDataSetChanged()
            }
        })
    }
}
