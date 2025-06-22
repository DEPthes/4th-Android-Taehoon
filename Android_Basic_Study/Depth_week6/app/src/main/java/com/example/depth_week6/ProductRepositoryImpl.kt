package com.example.depth_week6

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepositoryImpl(
    private val apiService: ProductApiService,
    private val favoriteRepository: FavoriteRepository
) : ProductRepository {

    override fun searchProducts(
        query: String,
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.searchProducts(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val products = response.body()?.products?.map { it.toProduct() } ?: emptyList()
                    // 찜하기 상태 확인해서 설정
                    updateFavoriteStatus(products, onSuccess)
                } else {
                    onError("검색 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }

    override fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getAllProducts().enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val products = response.body()?.products?.map { it.toProduct() } ?: emptyList()
                    updateFavoriteStatus(products, onSuccess)
                } else {
                    onError("데이터 로드 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }

    private fun updateFavoriteStatus(products: List<Product>, onSuccess: (List<Product>) -> Unit) {
        val favoriteIds = favoriteRepository.getFavoriteProductIds()
        products.forEach { product ->
            product.isFavorite = favoriteIds.contains(product.id)
        }
        onSuccess(products)
    }
}