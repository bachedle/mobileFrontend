package com.example.mobilefrontend.model

data class Card(
    val id: Int,
    var image_url: String,
    var name: String,
    var dataCardSet: String,
    var rarity: String,
    var code: String
)