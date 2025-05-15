package com.example.mobilefrontend.model


data class Collection (
    val userId: String,
    val cardId: String,
    val quantity: Int
)

data class AddCardToCollectionRequest (
    val user_id: Int,
    val card_id: Int
)