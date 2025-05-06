package com.example.mobilefrontend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilefrontend.databinding.ActivityAuthBinding
import androidx.core.content.edit

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // No manual fragment transaction needed; NavHost handles it
    }

    fun navigateToMainActivity() {
        val sharedPref = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        sharedPref.edit() { putBoolean("isAuthenticated", true) }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
