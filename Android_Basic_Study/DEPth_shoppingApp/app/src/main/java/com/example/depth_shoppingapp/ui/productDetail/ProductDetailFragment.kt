package com.example.depth_shoppingapp.ui.productDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.depth_shoppingapp.DB.AppDatabase
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.databinding.FragmentProductDetailBinding
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepositoryImpl
import com.example.depth_shoppingapp.ui.detail.ProductImageAdapter
import com.example.depth_shoppingapp.ui.myBag.MyBagRepository
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()

    // ViewModel 초기화 (Factory 패턴 사용)
    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(
            productRepository = ProductRepositoryImpl(),
            cartRepository = MyBagRepository(AppDatabase.getInstance(requireContext()).cartDAO()),
            context = requireContext().applicationContext
        )
    }

    private lateinit var imageAdapter: ProductImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
        loadProductDetail()
    }

    /**
     * 클릭 리스너 설정
     */
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            viewModel.onBackPressed()
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart()
        }
    }

    /**
     * ViewModel 관찰 설정
     */
    private fun observeViewModel() {
        // UI 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                handleUiState(uiState)
            }
        }

        // 상품 정보 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentProduct.collect { product ->
                product?.let {
                    bindProductDetail()
                }
            }
        }

        // 이벤트 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                event?.let {
                    handleEvent(it)
                    viewModel.onEventHandled()
                }
            }
        }
    }

    /**
     * UI 상태 처리
     */
    private fun handleUiState(uiState: ProductDetailUiState) {
        // 이미지 카운트 텍스트 업데이트
        binding.tvImageCount.text = uiState.imageCountText

        // 에러 상태 처리
        uiState.error?.let { error ->
            val errorMessage = getString(R.string.product_detail_loading_failed, error)
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 이벤트 처리
     */
    private fun handleEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.NavigateBack -> {
                findNavController().popBackStack()
            }
            is ProductDetailEvent.ShowMessage -> {
                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 상품 상세 정보 로드
     */
    private fun loadProductDetail() {
        val productId = args.productId
        viewModel.loadProductDetail(productId)
    }

    /**
     * 상품 상세 정보 UI 바인딩
     */
    private fun bindProductDetail() {
        val productInfo = viewModel.getFormattedProductInfo() ?: return

        // 상품 정보 바인딩
        binding.tvProductTitle.text = productInfo.title
        binding.tvProductPrice.text = productInfo.formattedPrice
        binding.tvProductRating.text = productInfo.rating
        binding.tvProductDescription.text = productInfo.description
        binding.tvProductBrand.text = productInfo.brand
        binding.tvProductCategory.text = productInfo.category
        binding.tvProductStock.text = productInfo.stock

        // 장바구니 버튼 활성화/비활성화
        binding.btnAddToCart.isEnabled = productInfo.hasStock
        binding.btnAddToCart.alpha = if (productInfo.hasStock) 1.0f else 0.5f

        // 이미지 슬라이드 설정
        setupImageSlider(productInfo.images)
    }

    /**
     * 이미지 슬라이더 설정
     */
    private fun setupImageSlider(images: List<String>) {
        imageAdapter = ProductImageAdapter(images)
        binding.viewPagerImages.adapter = imageAdapter

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tabLayoutImages, binding.viewPagerImages) { tab, position ->
            // 탭에 표시할 내용 (점 표시)
        }.attach()

        // 페이지 변경 리스너
        binding.viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onImagePageChanged(position)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}