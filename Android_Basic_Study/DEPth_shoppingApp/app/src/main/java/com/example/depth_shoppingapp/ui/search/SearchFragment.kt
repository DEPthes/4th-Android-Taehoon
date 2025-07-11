package com.example.depth_shoppingapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_shoppingapp.databinding.FragmentSearchBinding
import com.example.depth_shoppingapp.product.productDTO.ProductDTO
import com.example.depth_shoppingapp.ui.home.HomeViewAdapter
import com.example.depth_shoppingapp.ui.myBag.MyBagViewModelFactory

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var productAdapter: HomeViewAdapter

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

        initViewModel()
        initRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val factory = SearchViewModelFactory(requireContext())
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    private fun initRecyclerView() {
        productAdapter = HomeViewAdapter { product ->
            onProductClick(product)
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)

        binding.searchRecyclerView.apply {
            adapter = productAdapter
            this.layoutManager = layoutManager

            // 무한 스크롤 구현
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!searchViewModel.isCurrentlyLoading() &&
                        searchViewModel.hasMoreSearchData() &&
                        searchViewModel.getCurrentQuery().isNotEmpty()) {

                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                        if (lastVisibleItem >= totalItemCount - 5) {
                            searchViewModel.loadMoreSearchResults()
                        }
                    }
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearchQuery.text.toString()
            searchViewModel.searchProducts(query)
        }
    }

    private fun observeViewModel() {
        // 검색 결과 관찰
        searchViewModel.searchResults.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
        }

        // 로딩 상태 관찰
        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 로딩 UI 표시/숨김 (필요시 ProgressBar 추가)
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 에러 메시지 관찰
        searchViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                searchViewModel.onErrorMessageShown()
            }
        }

        // 토스트 메시지 관찰
        searchViewModel.toastMessage.observe(viewLifecycleOwner) { toastMessage ->
            toastMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                searchViewModel.onToastMessageShown()
            }
        }
    }

    private fun onProductClick(product: ProductDTO) {
        val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(product.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}