package com.pokellect.mobilefrontend

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.core.view.isVisible
import com.pokellect.mobilefrontend.repository.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pokellect.mobilefrontend.utils.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private var animationDrawable: AnimationDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This MUST be first!
        setContentView(R.layout.activity_main)

        // ✅ Now safe to access views
        val layout = findViewById<View>(R.id.main)
        animationDrawable = layout.background as? AnimationDrawable
        animationDrawable?.setEnterFadeDuration(500)
        animationDrawable?.setExitFadeDuration(500)

        // ✅ Now init your API service
        RetrofitService.init(this)

        // ✅ Load token
        val token = runBlocking {
            DataStoreManager.getValue(applicationContext).first()
        }

        // ✅ Setup NavGraph
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(if (token.isNotBlank()) R.id.nav_home else R.id.Login)
        navController.graph = graph

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.Login, R.id.Signup -> animationDrawable?.start()
                else -> animationDrawable?.stop()
            }
            bottomNavigation.isVisible = destination.id !in setOf(R.id.Login, R.id.Signup)
        }
    }
}
