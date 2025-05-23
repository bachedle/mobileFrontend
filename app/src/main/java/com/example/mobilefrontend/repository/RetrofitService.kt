package com.example.mobilefrontend.repository

import android.content.Context
import com.example.mobilefrontend.model.AddCardToCollectionRequest
import com.example.mobilefrontend.model.Collection
import com.example.mobilefrontend.model.Card
import com.example.mobilefrontend.model.LoginRequest
import com.example.mobilefrontend.model.SignUpRequest
import com.example.mobilefrontend.model.User
import com.example.mobilefrontend.model.UserProfile
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface RetrofitService {

    //Authentication
    @POST("/v1/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<User>>

    @POST("/v1/api/auth/signup")
    suspend fun signup(@Body request: SignUpRequest): Response<ApiResponse<User>>

    //User
    @GET("/v1/api/users/me")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfile>>

    //Cards
        @GET("/v1/api/cards/")
        suspend fun getCards(
            @Query("keyword") keyword: String?,
            @Query("rarity") rarity: String?
        ): Response<ApiResponse<List<Card>>>

    @POST("/v1/api/collections")
    suspend fun addCardToCollection(@Body request: AddCardToCollectionRequest): Response<ApiResponse<Collection>>

    @GET("/v1/api/collections/users/{userId}")
    suspend fun getCollectionByUserId(@Path("userId") userId: Int): Response<ApiResponse<List<Collection>>>

    @GET("v1/api/cards/random")
    suspend fun getRandomCards(@Query("series") series: String): Response<ApiResponse<List<Card>>>

    companion object {
        private const val BASE_URL = "https://yourrlove.com/"

        @Volatile
        private var instance: RetrofitService? = null

        /**
         * Call once at app startup (e.g. in Application.onCreate).
         * Afterwards you can use get() without passing Context.
         */
        fun init(context: Context) {
            if (instance != null) return
            synchronized(this) {
                if (instance == null) {
                    val logging = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    val client = OkHttpClient.Builder()
                        .addInterceptor(AuthInterceptor(context))
                        .addInterceptor(logging)
                        .build()

                    instance = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(RetrofitService::class.java)
                }
            }
        }

        /** After init(), safe to call from anywhere */
        fun get(): RetrofitService =
            instance ?: throw IllegalStateException("RetrofitService not initialized. Call init(context) first.")
    }
}