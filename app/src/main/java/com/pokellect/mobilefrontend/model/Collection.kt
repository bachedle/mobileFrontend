package com.pokellect.mobilefrontend.model


data class Collection (
    val userId: String,
    val cardId: String,
    val quantity: Int,
    val card: Card
)

data class AddCardToCollectionRequest (
    val user_id: Int,
    val card_id: Int
)