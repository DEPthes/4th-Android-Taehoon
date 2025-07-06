package com.example.depth_shoppingapp.ui.myBag

import com.example.depth_shoppingapp.DB.CartDAO
import com.example.depth_shoppingapp.DB.CartItem
import kotlinx.coroutines.flow.Flow

class MyBagRepository(private val cartDao: CartDAO) {

    fun getAllCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }

    suspend fun addToCart(cartItem: CartItem) {
        val existingItem = cartDao.getCartItemById(cartItem.productId)
        if (existingItem != null) {
            // 이미 존재하는 상품이면 수량 증가
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            cartDao.updateCartItem(updatedItem)
        } else {
            // 새로운 상품 추가
            cartDao.insertCartItem(cartItem)
        }
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        cartDao.updateCartItem(cartItem)
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        cartDao.deleteCartItemById(cartItem.productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }

    suspend fun getCartItemById(productId: Int): CartItem? {
        return cartDao.getCartItemById(productId)
    }
}