package com.example.mobilefrontend.di
import com.example.mobilefrontend.viewmodels.AuthViewModel
import com.example.mobilefrontend.viewmodels.CardViewModel
import com.example.mobilefrontend.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { UserViewModel(get()) }
    viewModel { CardViewModel() }
    viewModel { AuthViewModel(get()) }
}