package com.example.depth_shoppingapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_shoppingapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: HomeViewAdapter

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

        initRecyclerView()
        observeViewModel()
    }

    private fun initRecyclerView() {
        productAdapter = HomeViewAdapter { product ->
            homeViewModel.onProductClick(product.id)
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)

        binding.homeRecyclerView.apply {
            adapter = productAdapter
            this.layoutManager = layoutManager

            // 무한 스크롤 구현
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (homeViewModel.canLoadMore()) {
                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                        if (lastVisibleItem >= totalItemCount - 5) {
                            homeViewModel.loadMoreProducts()
                        }
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        // 상품 리스트 관찰
        homeViewModel.products.observe(viewLifecycleOwner, Observer { products ->
            productAdapter.updateProducts(products)
        })

        // 로딩 상태 관찰
        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // 로딩 UI 업데이트 (프로그레스바 등)
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // 에러 메시지 관찰
        homeViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                homeViewModel.clearError()
            }
        })

        // 상품 상세 페이지 이동 관찰
        homeViewModel.navigateToProductDetail.observe(viewLifecycleOwner, Observer { productId ->
            productId?.let {
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(it)
                findNavController().navigate(action)
                homeViewModel.onProductDetailNavigated()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}