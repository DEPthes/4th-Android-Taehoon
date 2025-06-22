package com.example.depth_week6

data class Product(
    val id: Int,
    val title: String,
    val price: Int,
    val thumbnail: String,
    var isFavorite: Boolean = false
)
