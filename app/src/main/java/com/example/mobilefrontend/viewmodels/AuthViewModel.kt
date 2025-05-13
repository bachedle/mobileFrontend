package com.example.mobilefrontend.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefrontend.model.LoginRequest
import com.example.mobilefrontend.model.SignUpRequest
import com.example.mobilefrontend.model.User
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.repository.RetrofitService
import com.example.mobilefrontend.repository.toResultFlow
import com.example.mobilefrontend.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context): ViewModel() {
    private val _userState = MutableStateFlow<ApiResult<User>?>(null)
    val userState: StateFlow<ApiResult<User>?> = _userState

    fun login(payload: LoginRequest) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.getInstance().login(payload)
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
                RetrofitService.getInstance().signup(payload)
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