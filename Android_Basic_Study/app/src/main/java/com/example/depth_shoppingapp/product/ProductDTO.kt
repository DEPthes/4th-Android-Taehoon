package com.example.depth_shoppingapp.product

data class ProductDTO(
    val id: Int,
    val title: String,
    val price: Int,
    val rating: Double,
    val thumbnail: String,
    val description: String,
    val category: String,
    val brand: String?, // nullable로 변경
    val stock: Int,
    val isFavorite: Boolean = false
)