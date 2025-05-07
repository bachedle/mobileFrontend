package com.example.mobilefrontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobilefrontend.databinding.ActivityMainBinding
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val isAuthenticated = sharedPref.getBoolean("isAuthenticated", false)

        if (!isAuthenticated) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Don't load MainActivity until authenticated
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[CardViewModel::class.java]

        // Observe the card state
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cardState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data // ✅ Use `data`, not `metadata`
                            Log.d("Card", "Card data: $cards")
                        }
                        is ApiResult.Error -> {
                            Log.e("Card", "Error: ${result.message}") // ✅ Use `message`, not `exception`
                        }
                        is ApiResult.Loading -> {
                            Log.d("Card", "Loading...") // ✅ Optional: show loading UI
                        }
                        null -> {
                            Log.d("Card", "Initial state")
                        }
                    }
                }
            }
        }

        // Trigger the API call
        viewModel.getCards()
        // Load the initial fragment and setup bottom navigation
        replaceFragment(Home())
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.nav_users -> {
                    replaceFragment(User())
                    true
                }
                R.id.nav_search -> {
                    replaceFragment(Search())
                    true
                }
                R.id.nav_simulator -> {
                    replaceFragment(Simulator())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


}