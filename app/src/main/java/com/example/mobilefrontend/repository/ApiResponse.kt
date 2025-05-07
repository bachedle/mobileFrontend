package com.example.mobilefrontend.repository

data class ApiResponse<T>(
    val message: String,
    val statusCode: Int,
    val metadata: T
)