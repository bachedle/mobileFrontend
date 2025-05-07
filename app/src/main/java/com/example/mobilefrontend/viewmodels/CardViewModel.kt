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
    private val _cardState = MutableStateFlow<ApiResult<List<Card>>?>(null)
    val cardState: StateFlow<ApiResult<List<Card>>?> = _cardState

    fun getCards() {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.getInstance().getCards() // Response<ApiResponse<List<Card>>>
            }.collect { result ->
                Log.d("CardViewModel", "Result: $result")

                // No need to re-wrap result.data — just pass it along
                _cardState.value = result
            }
        }
    }
}