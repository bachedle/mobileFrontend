package com.example.mobilefrontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefrontend.model.Card
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.repository.RetrofitService
import com.example.mobilefrontend.repository.toResultFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardViewModel: ViewModel() {
    private val _cardState = MutableStateFlow<ApiResult<Card>?>(null)
    val cardState: StateFlow<ApiResult<Card>?> = _cardState

    fun getCard() {
        viewModelScope.launch {
            toResultFlow { RetrofitService.getInstance().getCard() }
                .collect { result ->


                    Log.d("result", "$result")

                    _cardState.value = result
                }
        }
    }


}