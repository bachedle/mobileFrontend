package com.example.mobilefrontend.repository

import com.example.mobilefrontend.model.Card
import com.example.mobilefrontend.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitService {
    @POST("/v1/auth/login")
    suspend fun login(): Response<User>

    //
    @GET("v1/cards/")
    suspend fun getCards(): Response<ApiResponse<List<Card>>>

    companion object {
        private var retrofitService: RetrofitService? = null
        val intercepter = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(intercepter)
        }.build()

        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://yourrlove.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}