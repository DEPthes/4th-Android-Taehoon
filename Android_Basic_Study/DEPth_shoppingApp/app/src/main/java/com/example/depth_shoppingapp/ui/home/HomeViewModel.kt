package com.example.depth_shoppingapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.product.productDTO.ProductDTO
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepository
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepositoryImpl

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository: ProductRepository = ProductRepositoryImpl()

    // 상품 리스트
    private val _products = MutableLiveData<List<ProductDTO>>()
    val products: LiveData<List<ProductDTO>> = _products

    // 로딩 상태
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 에러 메시지
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // 페이지네이션 관련 변수
    private var currentPage = 0
    private val pageSize = 20
    private var hasMoreData = true

    // 현재 로드된 상품 리스트
    private val currentProducts = mutableListOf<ProductDTO>()

    init {
        loadProducts()
    }

    fun loadProducts() {
        if (_isLoading.value == true) return

        _isLoading.value = true
        currentPage = 0
        currentProducts.clear()

        productRepository.getAllProducts(
            limit = pageSize,
            skip = currentPage * pageSize,
            onSuccess = { products ->
                _isLoading.value = false
                hasMoreData = products.size == pageSize
                currentProducts.addAll(products)
                _products.value = currentProducts.toList()
                currentPage++
                clearError()
            },
            onError = { errorMessage ->
                _isLoading.value = false
                _error.value = getApplication<Application>().getString(R.string.product_load_error, errorMessage)
            }
        )
    }

    fun loadMoreProducts() {
        if (_isLoading.value == true || !hasMoreData) return

        _isLoading.value = true

        productRepository.getAllProducts(
            limit = pageSize,
            skip = currentPage * pageSize,
            onSuccess = { products ->
                _isLoading.value = false
                if (products.isEmpty()) {
                    hasMoreData = false
                } else {
                    hasMoreData = products.size == pageSize
                    currentProducts.addAll(products)
                    _products.value = currentProducts.toList()
                    currentPage++
                }
                clearError()
            },
            onError = { errorMessage ->
                _isLoading.value = false
                _error.value = getApplication<Application>().getString(R.string.product_load_more_error, errorMessage)
            }
        )
    }

    fun canLoadMore(): Boolean {
        return hasMoreData && _isLoading.value != true
    }

    fun refreshProducts() {
        loadProducts()
    }

    fun clearError() {
        _error.value = null
    }

    // 상품 클릭 이벤트를 위한 SingleLiveEvent 역할
    private val _navigateToProductDetail = MutableLiveData<Int?>()
    val navigateToProductDetail: LiveData<Int?> = _navigateToProductDetail

    fun onProductClick(productId: Int) {
        _navigateToProductDetail.value = productId
    }

    fun onProductDetailNavigated() {
        _navigateToProductDetail.value = null
    }
}