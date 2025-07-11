package com.example.depth_shoppingapp.product.productRetrofit

import com.example.depth_shoppingapp.product.productDTO.ProductDTO
import com.example.depth_shoppingapp.product.productDTO.ProductDetailDTO

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

    fun getProductById(
        productId: Int,
        onSuccess: (ProductDetailDTO) -> Unit,
        onError: (String) -> Unit
    )
}