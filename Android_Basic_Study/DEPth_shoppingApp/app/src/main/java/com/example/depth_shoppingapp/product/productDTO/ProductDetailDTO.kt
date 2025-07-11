package com.example.depth_shoppingapp.product.productDTO

data class ProductDetailDTO(
    val id: Int,
    val title: String,
    val price: Int,
    val rating: Double,
    val images: List<String>,
    val description: String,
    val category: String,
    val brand: String?,
    val stock: Int,
    val isFavorite: Boolean = false
)