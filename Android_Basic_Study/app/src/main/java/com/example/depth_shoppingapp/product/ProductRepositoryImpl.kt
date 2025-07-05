package com.example.depth_shoppingapp.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ProductRepositoryImpl : ProductRepository {
    private var retrofit : Retrofit = RetrofitClient.getInstance()
    private var service : ProductGetApiService = retrofit.create(ProductGetApiService::class.java)

    override fun searchProducts(
        query: String,
        limit: Int,
        skip: Int,
        onSuccess: (List<ProductDTO>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = service.searchProducts(query, limit, skip)
        call.enqueue(object : Callback<ProductResponseDTO> {
            override fun onResponse(call: Call<ProductResponseDTO>, response: Response<ProductResponseDTO>) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        val products = productResponse.products.map { it.toProduct() }
                        onSuccess(products)
                    } else {
                        onError("응답 데이터가 없습니다.")
                    }
                } else {
                    onError("서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProductResponseDTO>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }

    override fun getAllProducts(
        limit: Int,
        skip: Int,
        onSuccess: (List<ProductDTO>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = service.getAllProducts(limit, skip)
        call.enqueue(object : Callback<ProductResponseDTO> {
            override fun onResponse(call: Call<ProductResponseDTO>, response: Response<ProductResponseDTO>) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        val products = productResponse.products.map { it.toProduct() }
                        onSuccess(products)
                    } else {
                        onError("응답 데이터가 없습니다.")
                    }
                } else {
                    onError("서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProductResponseDTO>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }
}