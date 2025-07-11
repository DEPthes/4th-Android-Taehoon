package com.example.depth_shoppingapp.ui.myBag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyBagViewModel(
    private val repository: MyBagRepository,
    private val application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val ITEMS_PER_PAGE = 5
        private const val MAX_QUANTITY = 99
    }

    // UI 상태 관리
    private val _uiState = MutableStateFlow(MyBagUiState())
    val uiState: StateFlow<MyBagUiState> = _uiState.asStateFlow()

    // 에러 메시지
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // 성공 메시지
    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    // 선택된 아이템들
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems: StateFlow<Set<Int>> = _selectedItems.asStateFlow()

    // 전체 장바구니 아이템
    private val _allCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val allCartItems: StateFlow<List<CartItem>> = _allCartItems.asStateFlow()

    // 현재 페이지 아이템
    private val _currentPageItems = MutableStateFlow<List<CartItem>>(emptyList())
    val currentPageItems: StateFlow<List<CartItem>> = _currentPageItems.asStateFlow()

    // 페이지 정보
    private val _pageInfo = MutableStateFlow<PageInfo>(PageInfo())
    val pageInfo: StateFlow<PageInfo> = _pageInfo.asStateFlow()

    // 총 가격 정보
    private val _totalPrice = MutableStateFlow<TotalPriceInfo>(TotalPriceInfo())
    val totalPrice: StateFlow<TotalPriceInfo> = _totalPrice.asStateFlow()

    private var currentPage = 0

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                repository.getAllCartItems().collect { items ->
                    _allCartItems.value = items
                    updateCurrentPage()
                    updatePageInfo()
                    updateTotalPrice()
                }
            } catch (e: Exception) {
                _errorMessage.value = application.getString(R.string.my_bag_cart_loading_failed, e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            deleteItem(cartItem)
            return
        }

        if (newQuantity > MAX_QUANTITY) {
            _errorMessage.value = application.getString(R.string.my_bag_max_quantity, MAX_QUANTITY)
            return
        }

        viewModelScope.launch {
            try {
                val updatedItem = cartItem.copy(quantity = newQuantity)
                repository.updateCartItem(updatedItem)
            } catch (e: Exception) {
                _errorMessage.value = application.getString(R.string.my_bag_quantity_update_failed, e.message)
            }
        }
    }

    fun deleteItem(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                repository.deleteCartItem(cartItem)
                // 선택된 아이템에서도 제거
                _selectedItems.value = _selectedItems.value.minus(cartItem.productId)
                _successMessage.value = application.getString(R.string.my_bag_item_deleted)
            } catch (e: Exception) {
                _errorMessage.value = application.getString(R.string.my_bag_delete_failed, e.message)
            }
        }
    }

    fun toggleItemSelection(productId: Int) {
        val currentSelection = _selectedItems.value.toMutableSet()
        if (currentSelection.contains(productId)) {
            currentSelection.remove(productId)
        } else {
            currentSelection.add(productId)
        }
        _selectedItems.value = currentSelection
        updateTotalPrice()
    }

    fun toggleSelectAll() {
        val currentPageItemIds = _currentPageItems.value.map { it.productId }.toSet()
        val currentSelection = _selectedItems.value.toMutableSet()

        if (currentPageItemIds.all { it in currentSelection }) {
            // 현재 페이지의 모든 아이템이 선택되어 있으면 선택 해제
            currentSelection.removeAll(currentPageItemIds)
        } else {
            // 그렇지 않으면 현재 페이지의 모든 아이템 선택
            currentSelection.addAll(currentPageItemIds)
        }

        _selectedItems.value = currentSelection
        updateTotalPrice()
    }

    fun loadNextPage() {
        val totalPages = getTotalPages()
        if (currentPage < totalPages - 1) {
            currentPage++
            updateCurrentPage()
            updatePageInfo()
        }
    }

    fun loadPrevPage() {
        if (currentPage > 0) {
            currentPage--
            updateCurrentPage()
            updatePageInfo()
        }
    }

    private fun updateCurrentPage() {
        val startIndex = currentPage * ITEMS_PER_PAGE
        val endIndex = minOf(startIndex + ITEMS_PER_PAGE, _allCartItems.value.size)
        val pageItems = _allCartItems.value.subList(startIndex, endIndex)
        _currentPageItems.value = pageItems
    }

    private fun updatePageInfo() {
        val totalPages = getTotalPages()
        val hasNextPage = currentPage < totalPages - 1
        val hasPrevPage = currentPage > 0

        _pageInfo.value = PageInfo(
            currentPage = currentPage + 1,
            totalPages = totalPages,
            hasNextPage = hasNextPage,
            hasPrevPage = hasPrevPage
        )
    }

    private fun getTotalPages(): Int {
        return if (_allCartItems.value.isEmpty()) 1
        else (_allCartItems.value.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
    }

    private fun updateTotalPrice() {
        val selectedItemsList = getSelectedItemsList()
        val totalAmount = selectedItemsList.sumOf { it.price * it.quantity }
        val selectedCount = selectedItemsList.size

        _totalPrice.value = TotalPriceInfo(
            totalAmount = totalAmount,
            selectedCount = selectedCount,
            hasSelectedItems = selectedItemsList.isNotEmpty()
        )
    }

    fun getSelectedItemsList(): List<CartItem> {
        val selectedIds = _selectedItems.value
        return _allCartItems.value.filter { it.productId in selectedIds }
    }

    fun processOrder() {
        val selectedItemsList = getSelectedItemsList()
        if (selectedItemsList.isEmpty()) {
            _errorMessage.value = application.getString(R.string.my_bag_select_items)
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProcessingOrder = true)

                // 선택된 항목들을 DB에서 제거
                selectedItemsList.forEach { item ->
                    repository.deleteCartItem(item)
                }

                // 선택된 아이템 목록 초기화
                _selectedItems.value = emptySet()

                _successMessage.value = application.getString(R.string.my_bag_order_complete)
                _uiState.value = _uiState.value.copy(orderCompleted = true)

            } catch (e: Exception) {
                _errorMessage.value = application.getString(R.string.my_bag_order_failed, e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isProcessingOrder = false)
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }

    fun resetOrderState() {
        _uiState.value = _uiState.value.copy(orderCompleted = false)
    }

    fun isItemSelected(productId: Int): Boolean {
        return _selectedItems.value.contains(productId)
    }
}

// UI 상태 데이터 클래스
data class MyBagUiState(
    val isLoading: Boolean = false,
    val isProcessingOrder: Boolean = false,
    val orderCompleted: Boolean = false
)

// 페이지 정보 데이터 클래스
data class PageInfo(
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasNextPage: Boolean = false,
    val hasPrevPage: Boolean = false
)

// 총 가격 정보 데이터 클래스
data class TotalPriceInfo(
    val totalAmount: Int = 0,
    val selectedCount: Int = 0,
    val hasSelectedItems: Boolean = false
)

class MyBagViewModelFactory(
    private val repository: MyBagRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyBagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyBagViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}