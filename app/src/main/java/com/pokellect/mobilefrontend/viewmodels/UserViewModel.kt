package com.pokellect.mobilefrontend.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokellect.mobilefrontend.model.UserProfile
import com.pokellect.mobilefrontend.repository.ApiResult
import com.pokellect.mobilefrontend.repository.RetrofitService
import com.pokellect.mobilefrontend.repository.toResultFlow
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
