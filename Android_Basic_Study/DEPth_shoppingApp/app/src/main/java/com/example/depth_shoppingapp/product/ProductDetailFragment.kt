package com.example.depth_shoppingapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.depth_shoppingapp.DB.AppDatabase
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.databinding.FragmentProductDetailBinding
import com.example.depth_shoppingapp.product.ProductDetailDTO
import com.example.depth_shoppingapp.product.ProductRepository
import com.example.depth_shoppingapp.product.ProductRepositoryImpl
import com.example.depth_shoppingapp.ui.myBag.MyBagRepository
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: MyBagRepository
    private lateinit var imageAdapter: ProductImageAdapter

    private var currentProduct: ProductDetailDTO? = null

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

        initRepositories()
        setupClickListeners()
        loadProductDetail()
    }

    private fun initRepositories() {
        productRepository = ProductRepositoryImpl()
        val database = AppDatabase.getInstance(requireContext())
        cartRepository = MyBagRepository(database.cartDAO())
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun loadProductDetail() {
        val productId = args.productId

        productRepository.getProductById(
            productId = productId,
            onSuccess = { productDetail ->
                currentProduct = productDetail
                bindProductDetail(productDetail)
            },
            onError = { error ->
                Toast.makeText(requireContext(), "상품 정보 로드 실패: $error", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        )
    }

    private fun bindProductDetail(product: ProductDetailDTO) {
        // 상품 정보 바인딩
        binding.tvProductTitle.text = product.title
        binding.tvProductPrice.text = "₩${String.format("%,d", product.price)}"
        binding.tvProductRating.text = "★ ${product.rating}"
        binding.tvProductDescription.text = product.description
        binding.tvProductBrand.text = "브랜드: ${product.brand}"
        binding.tvProductCategory.text = "카테고리: ${product.category}"
        binding.tvProductStock.text = "재고: ${product.stock}개"

        // 이미지 슬라이드 설정
        setupImageSlider(product.images)
    }

    private fun setupImageSlider(images: List<String>) {
        imageAdapter = ProductImageAdapter(images)
        binding.viewPagerImages.adapter = imageAdapter

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tabLayoutImages, binding.viewPagerImages) { tab, position ->
            // 탭에 표시할 내용 (점 표시)
        }.attach()

        // 이미지 개수 표시
        binding.tvImageCount.text = "${1}/${images.size}"

        // 페이지 변경 리스너
        binding.viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvImageCount.text = "${position + 1}/${images.size}"
            }
        })
    }

    private fun addToCart() {
        val product = currentProduct ?: return

        if (product.stock <= 0) {
            Toast.makeText(requireContext(), "재고가 부족합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = CartItem(
            productId = product.id,
            title = product.title,
            price = product.price,
            thumbnail = product.images.firstOrNull() ?: "",
            quantity = 1
        )

        lifecycleScope.launch {
            try {
                cartRepository.addToCart(cartItem)
                Toast.makeText(requireContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "장바구니 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}