package com.example.depth_shoppingapp.ui.myBag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.depth_shoppingapp.DB.AppDatabase
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.databinding.FragmentMyBagBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MyBagFragment : Fragment() {

    private var _binding: FragmentMyBagBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MyBagViewModel
    private lateinit var cartAdapter: MyBagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val database = AppDatabase.getInstance(requireContext())
        val repository = MyBagRepository(database.cartDAO())

        val factory = MyBagViewModelFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[MyBagViewModel::class.java]
    }

    private fun setupRecyclerView() {
        cartAdapter = MyBagAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                viewModel.updateQuantity(cartItem, newQuantity)
            },
            onDeleteClicked = { cartItem ->
                showDeleteConfirmDialog(cartItem)
            },
            onSelectionChanged = { productId ->
                viewModel.toggleItemSelection(productId)
            }
        )

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnOrder.setOnClickListener {
            showOrderConfirmDialog()
        }

        binding.btnSelectAll.setOnClickListener {
            viewModel.toggleSelectAll()
        }

        binding.btnNextPage.setOnClickListener {
            viewModel.loadNextPage()
        }

        binding.btnPrevPage.setOnClickListener {
            viewModel.loadPrevPage()
        }
    }

    private fun observeViewModel() {
        // UI 상태 관찰
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateOrderProcessingState(state.isProcessingOrder)

                if (state.orderCompleted) {
                    handleOrderCompleted()
                }
            }
        }

        // 현재 페이지 아이템 관찰
        lifecycleScope.launch {
            viewModel.currentPageItems.collectLatest { items ->
                cartAdapter.submitList(items)
            }
        }

        // 선택된 아이템 관찰
        lifecycleScope.launch {
            viewModel.selectedItems.collectLatest { selectedIds ->
                cartAdapter.updateSelectedItems(selectedIds)
            }
        }

        // 페이지 정보 관찰
        lifecycleScope.launch {
            viewModel.pageInfo.collectLatest { pageInfo ->
                updatePageInfo(pageInfo)
            }
        }

        // 총 가격 정보 관찰
        lifecycleScope.launch {
            viewModel.totalPrice.collectLatest { totalPriceInfo ->
                updateTotalPriceDisplay(totalPriceInfo)
            }
        }

        // 에러 메시지 관찰
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }

        // 성공 메시지 관찰
        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }

    private fun updateOrderProcessingState(isProcessing: Boolean) {
        binding.btnOrder.isEnabled = !isProcessing
        binding.btnOrder.text = if (isProcessing) {
            getString(R.string.my_bag_processing)
        } else {
            getString(R.string.my_bag_order)
        }
    }

    private fun updatePageInfo(pageInfo: PageInfo) {
        binding.tvPageInfo.text = getString(R.string.my_bag_page_info, pageInfo.currentPage, pageInfo.totalPages)
        binding.btnPrevPage.isEnabled = pageInfo.hasPrevPage
        binding.btnNextPage.isEnabled = pageInfo.hasNextPage
    }

    private fun updateTotalPriceDisplay(totalPriceInfo: TotalPriceInfo) {
        binding.tvTotalPrice.text = getString(R.string.my_bag_total_amount, String.format("%,d", totalPriceInfo.totalAmount))
        binding.btnOrder.isEnabled = totalPriceInfo.hasSelectedItems
    }

    private fun showDeleteConfirmDialog(cartItem: CartItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.my_bag_delete_title))
            .setMessage(getString(R.string.my_bag_delete_message, cartItem.title))
            .setPositiveButton(getString(R.string.my_bag_delete_confirm)) { _, _ ->
                viewModel.deleteItem(cartItem)
            }
            .setNegativeButton(getString(R.string.my_bag_delete_cancel), null)
            .show()
    }

    private fun showOrderConfirmDialog() {
        val selectedItems = viewModel.getSelectedItemsList()
        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.my_bag_select_items), Toast.LENGTH_SHORT).show()
            return
        }

        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        val itemCount = selectedItems.size

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.my_bag_order_confirm_title))
            .setMessage(getString(R.string.my_bag_order_confirm_message, itemCount, String.format("%,d", totalPrice)))
            .setPositiveButton(getString(R.string.my_bag_order_confirm)) { _, _ ->
                viewModel.processOrder()
            }
            .setNegativeButton(getString(R.string.my_bag_order_cancel), null)
            .show()
    }

    private fun handleOrderCompleted() {
        Toast.makeText(requireContext(), getString(R.string.my_bag_order_complete), Toast.LENGTH_LONG).show()
        viewModel.resetOrderState()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}