package com.example.depth_shoppingapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_shoppingapp.databinding.FragmentHomeBinding
import com.example.depth_shoppingapp.product.ProductDTO
import com.example.depth_shoppingapp.product.ProductRepository
import com.example.depth_shoppingapp.product.ProductRepositoryImpl

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productRepository: ProductRepository
    private lateinit var productAdapter: HomeViewAdapter

    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false
    private var hasMoreData = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRepositories()
        initRecyclerView()
        loadProducts()
    }

    private fun initRepositories() {
        productRepository = ProductRepositoryImpl()
    }

    private fun initRecyclerView() {
        productAdapter = HomeViewAdapter { product ->
            onProductClick(product)
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)

        binding.homeRecyclerView.apply {
            adapter = productAdapter
            this.layoutManager = layoutManager

            // 무한 스크롤 구현
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!isLoading && hasMoreData) {
                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                        if (lastVisibleItem >= totalItemCount - 5) {
                            loadMoreProducts()
                        }
                    }
                }
            })
        }
    }

    private fun loadProducts() {
        if (isLoading) return

        isLoading = true
        currentPage = 0

        productRepository.getAllProducts(
            limit = pageSize,
            skip = currentPage * pageSize,
            onSuccess = { products ->
                isLoading = false
                hasMoreData = products.size == pageSize
                productAdapter.updateProducts(products)
                currentPage++
            },
            onError = { error ->
                isLoading = false
                Toast.makeText(requireContext(), "상품 로드 실패: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadMoreProducts() {
        if (isLoading || !hasMoreData) return

        isLoading = true

        productRepository.getAllProducts(
            limit = pageSize,
            skip = currentPage * pageSize,
            onSuccess = { products ->
                isLoading = false
                if (products.isEmpty()) {
                    hasMoreData = false
                } else {
                    hasMoreData = products.size == pageSize
                    productAdapter.addProducts(products)
                    currentPage++
                }
            },
            onError = { error ->
                isLoading = false
                Toast.makeText(requireContext(), "추가 상품 로드 실패: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun onProductClick(product: ProductDTO) {
        // DetailFragment로 이동하는 코드 (나중에 구현)
        Toast.makeText(requireContext(), "${product.title} 클릭됨", Toast.LENGTH_SHORT).show()

        // 예시: Navigation Component 사용 시
        // val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(product.id)
        // findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}