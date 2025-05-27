package com.pokellect.mobilefrontend.di
import com.pokellect.mobilefrontend.viewmodels.AuthViewModel
import com.pokellect.mobilefrontend.viewmodels.CardViewModel
import com.pokellect.mobilefrontend.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { UserViewModel(get()) }
    viewModel { CardViewModel() }
    viewModel { AuthViewModel(get()) }
}