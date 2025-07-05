package com.example.depth_shoppingapp.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_shoppingapp.databinding.FragmentHomeBinding
import com.example.depth_shoppingapp.databinding.FragmentSearchBinding
import com.example.depth_shoppingapp.product.ProductDTO
import com.example.depth_shoppingapp.product.ProductRepository
import com.example.depth_shoppingapp.product.ProductRepositoryImpl
import com.example.depth_shoppingapp.ui.home.HomeFragmentDirections
import com.example.depth_shoppingapp.ui.home.HomeViewAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var productRepository: ProductRepository
    private lateinit var productAdapter: HomeViewAdapter

    private lateinit var btnSearch: Button
    private lateinit var etSearchQuery: EditText

    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false
    private var hasMoreData = true
    private var currentQuery = "" // 현재 검색어 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRepositories()
        initRecyclerView()
        // 초기 로드 제거 - 검색 버튼 클릭 시에만 로드
    }

    private fun initRepositories() {
        productRepository = ProductRepositoryImpl()
    }

    private fun initRecyclerView() {
        btnSearch = binding.btnSearch
        etSearchQuery = binding.etSearchQuery

        btnSearch.setOnClickListener {
            searchProducts()
        }

        productAdapter = HomeViewAdapter { product ->
            onProductClick(product)
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)

        binding.searchRecyclerView.apply {
            adapter = productAdapter
            this.layoutManager = layoutManager

            // 무한 스크롤 구현 (검색 결과에 대해서만)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!isLoading && hasMoreData && currentQuery.isNotEmpty()) {
                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                        if (lastVisibleItem >= totalItemCount - 5) {
                            loadMoreSearchResults()
                        }
                    }
                }
            })
        }
    }

    private fun searchProducts() {
        val query = etSearchQuery.text.toString().trim()

        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 새로운 검색 시작
        currentQuery = query
        currentPage = 0
        isLoading = true
        hasMoreData = true

        productRepository.searchProducts(
            query = query,
            limit = pageSize,
            skip = 0,
            onSuccess = { products ->
                isLoading = false
                Log.d("ProductSearch", "검색 성공: ${products.size}개의 상품")

                // 검색 결과로 RecyclerView 업데이트
                productAdapter.updateProducts(products)

                if (products.isEmpty()) {
                    Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    hasMoreData = false
                } else {
                    Toast.makeText(requireContext(), "${products.size}개 상품을 찾았습니다.", Toast.LENGTH_SHORT).show()
                    hasMoreData = products.size == pageSize
                    currentPage = 1 // 다음 페이지로 설정
                }
            },
            onError = { errorMessage ->
                isLoading = false
                Log.e("ProductSearch", "검색 실패: $errorMessage")
                Toast.makeText(requireContext(), "검색 실패: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun loadMoreSearchResults() {
        if (isLoading || !hasMoreData || currentQuery.isEmpty()) return

        isLoading = true

        productRepository.searchProducts(
            query = currentQuery,
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
                Toast.makeText(requireContext(), "추가 검색 결과 로드 실패: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun onProductClick(product: ProductDTO) {
        // ProductDetailFragment로 이동
        val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(product.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}