package com.pokellect.mobilefrontend.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokellect.mobilefrontend.model.LoginRequest
import com.pokellect.mobilefrontend.model.SignUpRequest
import com.pokellect.mobilefrontend.model.User
import com.pokellect.mobilefrontend.repository.ApiResult
import com.pokellect.mobilefrontend.repository.RetrofitService
import com.pokellect.mobilefrontend.repository.toResultFlow
import com.pokellect.mobilefrontend.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context): ViewModel() {
    private val _userState = MutableStateFlow<ApiResult<User>?>(null)
    val userState: StateFlow<ApiResult<User>?> = _userState

    fun login(payload: LoginRequest) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().login(payload)
            }.collect { result ->
                _userState.value = result
                // Save token if login successful
                if (result is ApiResult.Success) {
                    val token = result.data?.accessToken  // Adjust based on your API response
                    if (token != null) {
                        DataStoreManager.saveValue(context, token)
                    }
                }
            }
        }
    }

    fun signup(payload: SignUpRequest) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().signup(payload)
            }.collect { result ->
                _userState.value = result
                // Save token if login successful
                if (result is ApiResult.Success) {
                    val token = result.data?.accessToken  // Adjust based on your API response
                    if (token != null) {
                        DataStoreManager.saveValue(context, token)
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            DataStoreManager.clearData(context)
        }
    }
}