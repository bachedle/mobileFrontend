package com.pokellect.mobilefrontend.model

data class User(
    val username: String,
    val email: String,
    val password: String,
    val accessToken: String,
)

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
)