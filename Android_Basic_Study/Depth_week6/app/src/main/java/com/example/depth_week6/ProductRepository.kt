package com.example.depth_week6

interface ProductRepository {
    fun searchProducts(
        query: String,
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    )

    fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    )
}