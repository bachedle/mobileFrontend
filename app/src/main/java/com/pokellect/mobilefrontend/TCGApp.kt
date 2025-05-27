package com.pokellect.mobilefrontend


import android.app.Application
import com.pokellect.mobilefrontend.di.viewModelsModule
import org.koin.android.ext.koin.androidContext      // ← this import
import org.koin.core.context.startKoin                // ← and this

class TCGApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // give Koin your Application context
            androidContext(this@TCGApp)
            // register all your modules
            modules(listOf(viewModelsModule))
        }
    }
}