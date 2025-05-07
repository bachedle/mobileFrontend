package com.example.mobilefrontend.repository

enum class ApiStatus{
    SUCCESS,
    ERROR,
    LOADING
}

sealed class ApiResult <out T> (val statusCode: Int?, val data: T?, val message:String?) {

    data class Success<out R>(val _data: R?): ApiResult<R>(
        statusCode = 200,
        data = _data,
        message = "concac"
    )

    data class Error(val exception: String): ApiResult<Nothing>(
        statusCode = 400,
        data = null,
        message = exception
    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean): ApiResult<R>(
        statusCode = 500,
        data = _data,
        message = null
    )

}