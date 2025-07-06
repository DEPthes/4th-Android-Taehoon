package com.example.depth_shoppingapp.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductGetApiService {
    @GET("products/search")
    fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("skip") skip: Int = 0
    ): Call<ProductResponseDTO>

    @GET("products")
    fun getAllProducts(
        @Query("limit") limit: Int = 20,
        @Query("skip") skip: Int = 0
    ): Call<ProductResponseDTO>

    @GET("products/{id}")
    fun getProductById(
        @Path("id") productId: Int
    ): Call<ProductDetailResponseDTO>
}