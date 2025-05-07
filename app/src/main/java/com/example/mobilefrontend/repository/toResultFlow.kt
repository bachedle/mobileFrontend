package com.example.mobilefrontend.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import retrofit2.Response

fun <T> toResultFlow(call: suspend () -> Response<ApiResponse<T>>): Flow<ApiResult<T>> {
    return flow {
        emit(ApiResult.Loading())

        try {
            val response = call()

            if (response.isSuccessful && response.body() != null) {
                val apiBody = response.body()!!

                emit(
                    ApiResult.Success(
                        data = apiBody.metadata,
                        statusCode = apiBody.statusCode,
                        message = apiBody.message
                    )
                )
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                emit(ApiResult.Error(errorMsg, statusCode = response.code()))
            }

        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unexpected exception"))
        }

    }.flowOn(Dispatchers.IO)
}
