package com.example.depth_week6

interface FavoriteRepository {
    fun addToFavorite(productId: Int)
    fun removeFromFavorite(productId: Int)
    fun isFavorite(productId: Int): Boolean
    fun getFavoriteProductIds(): Set<Int>
}
