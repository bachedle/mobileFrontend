package com.pokellect.mobilefrontend.repository

import android.content.Context
import com.pokellect.mobilefrontend.utils.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// 1) Create an interceptor that adds the header
class AuthInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // block briefly to get the latest token
        val token = runBlocking {
            DataStoreManager.getValue(context).first()
        }

        val original = chain.request()
        val request = if (token.isNotBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
