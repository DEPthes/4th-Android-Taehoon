package com.example.depth_week6

import com.example.depth_week6.FavoriteRepository
import com.example.depth_week6.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavoriteRepositoryImpl(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun addToFavorite(productId: Int) {

    }

    fun addToFavorite(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteEntity = FavoriteEntity(
                productId = product.id,
                title = product.title,
                price = product.price,
                thumbnail = product.thumbnail
            )
            favoriteDao.addFavorite(favoriteEntity)
        }
    }

    override fun removeFromFavorite(productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            favoriteDao.removeFavorite(productId)
        }
    }

    override fun isFavorite(productId: Int): Boolean {
        return runBlocking {
            favoriteDao.isFavorite(productId)
        }
    }

    override fun getFavoriteProductIds(): Set<Int> {
        return runBlocking {
            favoriteDao.getFavoriteProductIds().toSet()
        }
    }

    fun getAllFavorites(): Flow<List<Product>> {
        return favoriteDao.getAllFavorites().map { favorites ->
            favorites.map { favorite ->
                Product(
                    id = favorite.productId,
                    title = favorite.title,
                    price = favorite.price,
                    thumbnail = favorite.thumbnail,
                    isFavorite = true
                )
            }
        }
    }

    suspend fun getAllFavoritesSync(): List<Product> {
        return favoriteDao.getAllFavoritesSync().map { favorite ->
            Product(
                id = favorite.productId,
                title = favorite.title,
                price = favorite.price,
                thumbnail = favorite.thumbnail,
                isFavorite = true
            )
        }
    }

    fun clearAllFavorites() {
        CoroutineScope(Dispatchers.IO).launch {
            favoriteDao.clearAllFavorites()
        }
    }
}