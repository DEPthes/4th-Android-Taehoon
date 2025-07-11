package com.example.depth_shoppingapp.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.product.productDTO.ProductDTO
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepository
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepositoryImpl
import kotlinx.coroutines.launch

class SearchViewModel(private val context: Context) : ViewModel() {

    private val productRepository: ProductRepository = ProductRepositoryImpl()

    // UI 상태 관리
    private val _searchResults = MutableLiveData<List<ProductDTO>>()
    val searchResults: LiveData<List<ProductDTO>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String> = _errorMessage as LiveData<String>

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String> = _toastMessage as LiveData<String>

    private val _hasMoreData = MutableLiveData<Boolean>()
    val hasMoreData: LiveData<Boolean> = _hasMoreData

    // 검색 상태 관리
    private var currentQuery = ""
    private var currentPage = 0
    private val pageSize = 20
    private val currentProducts = mutableListOf<ProductDTO>()

    fun searchProducts(query: String) {
        if (query.trim().isEmpty()) {
            _toastMessage.value = context.getString(R.string.search_enter_query)
            return
        }

        // 새로운 검색 시작
        currentQuery = query.trim()
        currentPage = 0
        currentProducts.clear()
        _isLoading.value = true
        _hasMoreData.value = true

        viewModelScope.launch {
            performSearch(query = currentQuery, skip = 0, isNewSearch = true)
        }
    }

    fun loadMoreSearchResults() {
        if (_isLoading.value == true || _hasMoreData.value == false || currentQuery.isEmpty()) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            performSearch(query = currentQuery, skip = currentPage * pageSize, isNewSearch = false)
        }
    }

    private suspend fun performSearch(query: String, skip: Int, isNewSearch: Boolean) {
        productRepository.searchProducts(
            query = query,
            limit = pageSize,
            skip = skip,
            onSuccess = { products ->
                _isLoading.value = false
                handleSearchSuccess(products, isNewSearch)
            },
            onError = { errorMessage ->
                _isLoading.value = false
                handleSearchError(errorMessage, isNewSearch)
            }
        )
    }

    private fun handleSearchSuccess(products: List<ProductDTO>, isNewSearch: Boolean) {
        Log.d("SearchViewModel", "검색 성공: ${products.size}개의 상품")

        if (isNewSearch) {
            // 새로운 검색 결과
            currentProducts.clear()
            currentProducts.addAll(products)
            _searchResults.value = currentProducts.toList()

            if (products.isEmpty()) {
                _toastMessage.value = context.getString(R.string.search_no_results)
                _hasMoreData.value = false
            } else {
                _toastMessage.value = context.getString(R.string.search_found_products, products.size)
                _hasMoreData.value = products.size == pageSize
                currentPage = 1
            }
        } else {
            // 추가 검색 결과 (무한 스크롤)
            if (products.isEmpty()) {
                _hasMoreData.value = false
            } else {
                _hasMoreData.value = products.size == pageSize
                currentProducts.addAll(products)
                _searchResults.value = currentProducts.toList()
                currentPage++
            }
        }
    }

    private fun handleSearchError(errorMessage: String, isNewSearch: Boolean) {
        Log.e("SearchViewModel", "검색 실패: $errorMessage")

        if (isNewSearch) {
            _errorMessage.value = context.getString(R.string.search_failed, errorMessage)
        } else {
            _toastMessage.value = context.getString(R.string.search_load_more_failed, errorMessage)
        }
    }

    fun getCurrentQuery(): String = currentQuery

    fun isCurrentlyLoading(): Boolean = _isLoading.value == true

    fun hasMoreSearchData(): Boolean = _hasMoreData.value == true

    // 에러 메시지 소비 후 초기화
    fun onErrorMessageShown() {
        _errorMessage.value = null
    }

    // 토스트 메시지 소비 후 초기화
    fun onToastMessageShown() {
        _toastMessage.value = null
    }
}

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}