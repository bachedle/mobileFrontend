package com.pokellect.mobilefrontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokellect.mobilefrontend.model.AddCardToCollectionRequest
import com.pokellect.mobilefrontend.model.Card
import com.pokellect.mobilefrontend.model.Collection
import com.pokellect.mobilefrontend.repository.ApiResult
import com.pokellect.mobilefrontend.repository.RetrofitService
import com.pokellect.mobilefrontend.repository.toResultFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CardViewModel: ViewModel() {
    private val _cardState = MutableStateFlow<ApiResult<List<Card>>?>(null)
    val cardState: StateFlow<ApiResult<List<Card>>?> = _cardState

    private val _collectionState = MutableStateFlow<ApiResult<Collection>?>(null)
    val collectionState: StateFlow<ApiResult<Collection>?> = _collectionState

    private val _userCollectionState = MutableStateFlow<ApiResult<List<Collection>>?>(null)
    val userCollectionState: StateFlow<ApiResult<List<Collection>>?> = _userCollectionState

    private val _randomCardsState = MutableStateFlow<ApiResult<List<Card>>?>(null)
    val randomCardsState: StateFlow<ApiResult<List<Card>>?> = _randomCardsState

    private val _cardSearchState = MutableStateFlow<ApiResult<List<Card>>?>(null)
    val cardSearchState: StateFlow<ApiResult<List<Card>>?> = _cardSearchState

        fun getCards(keyword: String? = null, rarity: String? = null) {
            viewModelScope.launch {
                toResultFlow {
                    RetrofitService.get().getCards(keyword, rarity)
                }.collect { result ->
                    Log.d("CardViewModel", "Result: $result")
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

    fun searchCards(imageFile: File) {
        viewModelScope.launch {
            val requestFile = imageFile
                .asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "image", imageFile.name, requestFile
            )
            Log.d("CardViewModel", "Multipart body: $multipartBody")
            toResultFlow {
                RetrofitService.get().searchCards(multipartBody)
            }.collect { result ->
                _cardState.value = result
                Log.d("CardViewModel", "Search cards result: $result")
            }
        }
    }
}