package com.example.mobilefrontend.repository

import com.example.mobilefrontend.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {
    @POST("/v1/auth/login")
    suspend fun login() : Response<User>
//
//    @GET("v1/cards/")
//    suspend fun getCard() : Response<Card>

            companion object {
        private var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://yourrlove.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}