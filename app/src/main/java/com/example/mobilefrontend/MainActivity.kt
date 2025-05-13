package com.example.mobilefrontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.mobilefrontend.utils.DataStoreManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val authScreens = setOf(R.id.Login, R.id.Signup)
            bottomNavigation.isVisible = destination.id !in authScreens
        }

        // 🔐 Auto-login check using DataStoreManager object
        lifecycleScope.launch {
            DataStoreManager.getValue(applicationContext).collect { token ->
                if (token.isNotEmpty()) {
                    navController.navigate(R.id.action_login_to_home)
                }
            }
        }
    }
}