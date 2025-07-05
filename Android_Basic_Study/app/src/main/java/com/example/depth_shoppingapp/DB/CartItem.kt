package com.example.depth_shoppingapp.DB

import androidx.room.*

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val productId: Int,
    val title: String,
    val price: Int,
    val thumbnail: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)