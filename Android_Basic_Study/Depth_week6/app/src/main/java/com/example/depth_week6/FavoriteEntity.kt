package com.example.depth_week6

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val productId: Int,
    val title: String,
    val price: Int,
    val thumbnail: String,
    val addedAt: Long = System.currentTimeMillis()
)