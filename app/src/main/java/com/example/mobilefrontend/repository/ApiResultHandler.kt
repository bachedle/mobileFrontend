package com.example.mobilefrontend.repository

import androidx.lifecycle.MutableLiveData


class ApiResultHandler<T>(private val onSuccess: (T?) -> Unit, private val onFailure: (T?) -> Unit)  {


    private var loading = MutableLiveData<Boolean>()

    fun handleApiResult(result: ApiResult<T?>) {
        when (result.statusCode) {
            500 -> {
                loading.value = true
            }

            200 -> {
                loading.value = false
                onSuccess(result.data)
            }

            400-> {
                loading.value = false
                onFailure(result.data)
            }
        }
    }
}