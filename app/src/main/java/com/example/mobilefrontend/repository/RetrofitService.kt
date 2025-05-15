package com.example.mobilefrontend.repository

import com.example.mobilefrontend.model.AddCardToCollectionRequest
import com.example.mobilefrontend.model.Collection
import com.example.mobilefrontend.model.Card
import com.example.mobilefrontend.model.LoginRequest
import com.example.mobilefrontend.model.SignUpRequest
import com.example.mobilefrontend.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitService {
    @POST("/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<User>>

    @POST("/v1/auth/signup")
    suspend fun signup(@Body request: SignUpRequest): Response<ApiResponse<User>>

    //
    @GET("v1/cards/")
    suspend fun getCards(): Response<ApiResponse<List<Card>>>

    @POST("/v1/collections")
    suspend fun addCardToCollection(@Body request: AddCardToCollectionRequest): Response<ApiResponse<Collection>>

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