package com.example.depth_week6

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    @GET("products/search")
    fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): Call<SearchResponse>

    @GET("products")
    fun getAllProducts(
        @Query("limit") limit: Int = 20
    ): Call<SearchResponse>
}