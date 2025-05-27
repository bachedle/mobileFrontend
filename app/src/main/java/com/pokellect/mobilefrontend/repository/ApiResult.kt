package com.pokellect.mobilefrontend.repository

enum class ApiStatus{
    SUCCESS,
    ERROR,
    LOADING
}

sealed class ApiResult<out T>(
    val statusCode: Int? = null,
    val data: T? = null,
    val message: String? = null
) {
    class Success<out T>(data: T?, statusCode: Int = 200, message: String? = null) :
        ApiResult<T>(statusCode, data, message)

    class Error(message: String, statusCode: Int = 400) :
        ApiResult<Nothing>(statusCode, null, message)

    class Loading<out T>(data: T? = null) :
        ApiResult<T>(null, data)
}