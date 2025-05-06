package com.example.mobilefrontend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mobilefrontend.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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