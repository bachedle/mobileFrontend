package com.pokellect.mobilefrontend.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String
)