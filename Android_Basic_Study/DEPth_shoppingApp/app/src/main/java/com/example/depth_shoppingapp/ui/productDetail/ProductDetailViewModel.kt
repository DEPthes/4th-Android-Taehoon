package com.example.depth_shoppingapp.ui.productDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.product.productDTO.ProductDetailDTO
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepository
import com.example.depth_shoppingapp.ui.myBag.MyBagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: MyBagRepository,
    private val context: Context
) : ViewModel() {

    // UI 상태 관리
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // 현재 상품 정보
    private val _currentProduct = MutableStateFlow<ProductDetailDTO?>(null)
    val currentProduct: StateFlow<ProductDetailDTO?> = _currentProduct.asStateFlow()

    // 이벤트 처리
    private val _events = MutableStateFlow<ProductDetailEvent?>(null)
    val events: StateFlow<ProductDetailEvent?> = _events.asStateFlow()

    /**
     * 상품 상세 정보 로드
     */
    fun loadProductDetail(productId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        productRepository.getProductById(
            productId = productId,
            onSuccess = { productDetail ->
                _currentProduct.value = productDetail
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            },
            onError = { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error
                )
                _events.value = ProductDetailEvent.NavigateBack
            }
        )
    }

    /**
     * 장바구니에 상품 추가
     */
    fun addToCart() {
        val product = _currentProduct.value ?: return

        // 재고 확인
        if (product.stock <= 0) {
            _events.value = ProductDetailEvent.ShowMessage(
                context.getString(R.string.product_detail_out_of_stock)
            )
            return
        }

        // 장바구니 아이템 생성
        val cartItem = CartItem(
            productId = product.id,
            title = product.title,
            price = product.price,
            thumbnail = product.images.firstOrNull() ?: "",
            quantity = 1
        )

        viewModelScope.launch {
            try {
                cartRepository.addToCart(cartItem)
                _events.value = ProductDetailEvent.ShowMessage(
                    context.getString(R.string.product_detail_added_to_cart)
                )
            } catch (e: Exception) {
                _events.value = ProductDetailEvent.ShowMessage(
                    context.getString(R.string.product_detail_add_to_cart_failed, e.message)
                )
            }
        }
    }

    /**
     * 이미지 슬라이더 페이지 변경
     */
    fun onImagePageChanged(position: Int) {
        val product = _currentProduct.value ?: return
        val imageCount = product.images.size
        _uiState.value = _uiState.value.copy(
            currentImageIndex = position,
            imageCountText = context.getString(R.string.product_detail_image_count, position + 1, imageCount)
        )
    }

    /**
     * 뒤로가기 버튼 클릭
     */
    fun onBackPressed() {
        _events.value = ProductDetailEvent.NavigateBack
    }

    /**
     * 이벤트 처리 완료
     */
    fun onEventHandled() {
        _events.value = null
    }

    /**
     * 상품 정보를 UI에 표시할 수 있는 형태로 변환
     */
    fun getFormattedProductInfo(): FormattedProductInfo? {
        val product = _currentProduct.value ?: return null
        val productStringRating = product.rating.toString()

        return FormattedProductInfo(
            title = product.title,
            formattedPrice = context.getString(R.string.product_price_format, String.format("%,d", product.price)),
            rating = context.getString(R.string.product_detail_rating, productStringRating),
            description = product.description,
            brand = context.getString(R.string.product_detail_brand, product.brand),
            category = context.getString(R.string.product_detail_category, product.category),
            stock = context.getString(R.string.product_detail_stock, product.stock),
            images = product.images,
            hasStock = product.stock > 0
        )
    }
}

/**
 * UI 상태 데이터 클래스
 */
data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentImageIndex: Int = 0,
    val imageCountText: String = "1/1"
)

/**
 * 포맷팅된 상품 정보 데이터 클래스
 */
data class FormattedProductInfo(
    val title: String,
    val formattedPrice: String,
    val rating: String,
    val description: String,
    val brand: String,
    val category: String,
    val stock: String,
    val images: List<String>,
    val hasStock: Boolean
)

/**
 * 이벤트 처리를 위한 sealed class
 */
sealed class ProductDetailEvent {
    object NavigateBack : ProductDetailEvent()
    data class ShowMessage(val message: String) : ProductDetailEvent()
}

/**
 * ViewModel Factory
 */
class ProductDetailViewModelFactory(
    private val productRepository: ProductRepository,
    private val cartRepository: MyBagRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(productRepository, cartRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}