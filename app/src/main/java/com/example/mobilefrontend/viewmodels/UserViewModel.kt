package com.example.mobilefrontend.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefrontend.model.User
import com.example.mobilefrontend.model.UserProfile
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.repository.RetrofitService
import com.example.mobilefrontend.repository.toResultFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val context: Context): ViewModel() {
    private val _userProfileState = MutableStateFlow<ApiResult<UserProfile>?>(null)
    val userProfileState: StateFlow<ApiResult<UserProfile>?> = _userProfileState

    init {
        Log.d("UserViewModel", "init")
        getUserProfile()
    }

    fun getUserProfile(){
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().getUserProfile()
            }.collect { result ->
                _userProfileState.value = result
            }
        }
    }
}
