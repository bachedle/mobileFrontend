package com.example.mobilefrontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefrontend.model.LoginRequest
import com.example.mobilefrontend.model.User
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.repository.RetrofitService
import com.example.mobilefrontend.repository.toResultFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val _userState = MutableStateFlow<ApiResult<User>?>(null)
    val userState: StateFlow<ApiResult<User>?> = _userState

    fun login(payload: LoginRequest) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.getInstance().login(payload)
            }.collect { result ->
                _userState.value = result
            }
        }
    }
}