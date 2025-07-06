package com.example.depth_shoppingapp.ui.myBag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.depth_shoppingapp.DB.AppDatabase
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.databinding.FragmentMyBagBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MyBagFragment : Fragment() {

    private var _binding: FragmentMyBagBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartRepository: MyBagRepository
    private lateinit var cartAdapter: MyBagAdapter

    private var currentPage = 0
    private val itemsPerPage = 5
    private var allCartItems = listOf<CartItem>()
    private var isLoading = false

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

        initRepository()
        setupRecyclerView()
        setupClickListeners()
        observeCartItems()
    }

    private fun initRepository() {
        val database = AppDatabase.getInstance(requireContext())
        cartRepository = MyBagRepository(database.cartDAO())
    }

    private fun setupRecyclerView() {
        cartAdapter = MyBagAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                updateQuantity(cartItem, newQuantity)
            },
            onDeleteClicked = { cartItem ->
                deleteItem(cartItem)
            },
            onSelectionChanged = {
                updateTotalPrice()
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
            processOrder()
        }

        binding.btnSelectAll.setOnClickListener {
            cartAdapter.toggleSelectAll()
            updateTotalPrice()
        }

        binding.btnNextPage.setOnClickListener {
            loadNextPage()
        }

        binding.btnPrevPage.setOnClickListener {
            loadPrevPage()
        }
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            cartRepository.getAllCartItems().collect { items ->
                allCartItems = items
                loadCurrentPage()
                updatePageInfo()
            }
        }
    }

    private fun loadCurrentPage() {
        val startIndex = currentPage * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, allCartItems.size)
        val pageItems = allCartItems.subList(startIndex, endIndex)

        cartAdapter.submitList(pageItems)
        updateTotalPrice()

        // 페이지 버튼 활성화/비활성화
        binding.btnPrevPage.isEnabled = currentPage > 0
        binding.btnNextPage.isEnabled = endIndex < allCartItems.size
    }

    private fun loadNextPage() {
        val totalPages = (allCartItems.size + itemsPerPage - 1) / itemsPerPage
        if (currentPage < totalPages - 1) {
            currentPage++
            loadCurrentPage()
            updatePageInfo()
        }
    }

    private fun loadPrevPage() {
        if (currentPage > 0) {
            currentPage--
            loadCurrentPage()
            updatePageInfo()
        }
    }

    private fun updatePageInfo() {
        val totalPages = if (allCartItems.isEmpty()) 1 else (allCartItems.size + itemsPerPage - 1) / itemsPerPage
        binding.tvPageInfo.text = "페이지 ${currentPage + 1} / $totalPages"
    }

    private fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            deleteItem(cartItem)
            return
        }

        lifecycleScope.launch {
            try {
                val updatedItem = cartItem.copy(quantity = newQuantity)
                cartRepository.updateCartItem(updatedItem)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "수량 업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteItem(cartItem: CartItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("상품 삭제")
            .setMessage("'${cartItem.title}'을(를) 장바구니에서 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                lifecycleScope.launch {
                    try {
                        cartRepository.deleteCartItem(cartItem)
                        Toast.makeText(requireContext(), "상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "삭제 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateTotalPrice() {
        val selectedItems = cartAdapter.getSelectedItems()
        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        binding.tvTotalPrice.text = "총 금액: ₩${String.format("%,d", totalPrice)}"

        // 선택된 항목이 있을 때만 주문 버튼 활성화
        binding.btnOrder.isEnabled = selectedItems.isNotEmpty()
    }

    private fun processOrder() {
        val selectedItems = cartAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "주문할 상품을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        val itemCount = selectedItems.size

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("주문 확인")
            .setMessage("총 ${itemCount}개 상품\n총 금액: ₩${String.format("%,d", totalPrice)}\n\n주문하시겠습니까?")
            .setPositiveButton("주문하기") { _, _ ->
                lifecycleScope.launch {
                    try {
                        // 선택된 항목들을 DB에서 제거
                        selectedItems.forEach { item ->
                            cartRepository.deleteCartItem(item)
                        }

                        Toast.makeText(requireContext(), "주문이 완료되었습니다!", Toast.LENGTH_LONG).show()

                        // 홈 화면으로 이동
                        findNavController().popBackStack()

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "주문 처리 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}