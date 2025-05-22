package com.example.mobilefrontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefrontend.model.AddCardToCollectionRequest
import com.example.mobilefrontend.model.Card
import com.example.mobilefrontend.model.Collection
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.repository.RetrofitService
import com.example.mobilefrontend.repository.toResultFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardViewModel: ViewModel() {
    private val _cardState = MutableStateFlow<ApiResult<List<Card>>?>(null)
    val cardState: StateFlow<ApiResult<List<Card>>?> = _cardState

    private val _collectionState = MutableStateFlow<ApiResult<Collection>?>(null)
    val collectionState: StateFlow<ApiResult<Collection>?> = _collectionState

    private val _userCollectionState = MutableStateFlow<ApiResult<List<Collection>>?>(null)
    val userCollectionState: StateFlow<ApiResult<List<Collection>>?> = _userCollectionState

    private val _randomCardsState = MutableStateFlow<ApiResult<List<Card>>?>(null)
    val randomCardsState: StateFlow<ApiResult<List<Card>>?> = _randomCardsState

    fun getCards() {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().getCards() // Response<ApiResponse<List<Card>>>
            }.collect { result ->
                Log.d("CardViewModel", "Result: $result")

                // No need to re-wrap result.data — just pass it along
                _cardState.value = result
            }
        }
    }

    fun addToCollection(payload: AddCardToCollectionRequest) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().addCardToCollection(payload)
            }.collect { result ->
                _collectionState.value = result
                Log.d("CardViewModel", "Result: $result")
            }
        }
    }

    fun resetCollectionState() {
        _collectionState.value = null
        Log.d("CardViewModel", "Reset collection state")
    }

    fun getUserCollection(userId: Int) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().getCollectionByUserId(userId)
            }.collect { result ->
                _userCollectionState.value = result
                Log.d("CardViewModel", "User collection result: $result")
            }
        }
    }

    fun getRandomCards(series: String) {
        viewModelScope.launch {
            toResultFlow {
                RetrofitService.get().getRandomCards(series)
            }.collect { result ->
                _randomCardsState.value = result
                Log.d("CardViewModel", "Random cards result: $result")
            }
        }
    }

    fun resetUserCollectionState() {
        _userCollectionState.value = null
        Log.d("CardViewModel", "Reset user collection state")
    }
}