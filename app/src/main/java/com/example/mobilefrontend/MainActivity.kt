package com.example.mobilefrontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import com.example.mobilefrontend.repository.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.mobilefrontend.utils.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitService.init(this)
        val token = runBlocking {
            DataStoreManager.getValue(applicationContext).first()
        }

        setContentView(R.layout.activity_main)

        // 2. Inflate your NavGraph and override its startDestination
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)

        graph.setStartDestination(
            if (token.isNotBlank()) R.id.nav_home
            else R.id.Login
        )
        navController.graph = graph


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigation.isVisible = destination.id !in setOf(R.id.Login, R.id.Signup)
        }
    }
}