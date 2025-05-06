package com.example.mobilefrontend.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getUser() {
        viewModelScope.launch {
            toResultFlow { RetrofitService.getInstance().login() }
                .collect { result ->
                    _userState.value = result
                }
        }
    }
}