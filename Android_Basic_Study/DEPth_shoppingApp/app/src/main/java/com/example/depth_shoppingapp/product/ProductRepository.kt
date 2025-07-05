package com.example.depth_shoppingapp.product

interface ProductRepository {
    fun searchProducts(
        query: String,
        limit: Int = 20,
        skip: Int = 0,
        onSuccess: (List<ProductDTO>) -> Unit,
        onError: (String) -> Unit
    )

    fun getAllProducts(
        limit: Int = 20,
        skip: Int = 0,
        onSuccess: (List<ProductDTO>) -> Unit,
        onError: (String) -> Unit
    )
}