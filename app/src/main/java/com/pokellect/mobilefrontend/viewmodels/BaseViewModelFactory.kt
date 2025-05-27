package com.pokellect.mobilefrontend.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaseViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return creator() as T
        } catch (e: Exception) {
            throw IllegalArgumentException("Unknown ViewModel class", e)
        }
    }
}