package com.example.depth_shoppingapp.ui.myBag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.databinding.ItemMyBagBinding

class MyBagAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteClicked: (CartItem) -> Unit,
    private val onSelectionChanged: (Int) -> Unit
) : ListAdapter<CartItem, MyBagAdapter.MyBagViewHolder>(CartItemDiffCallback()) {

    private var selectedItems = setOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBagViewHolder {
        val binding = ItemMyBagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyBagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyBagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateSelectedItems(selectedIds: Set<Int>) {
        val oldSelectedItems = selectedItems
        selectedItems = selectedIds

        // 변경된 아이템만 업데이트
        currentList.forEachIndexed { index, cartItem ->
            val wasSelected = oldSelectedItems.contains(cartItem.productId)
            val isSelected = selectedIds.contains(cartItem.productId)

            if (wasSelected != isSelected) {
                notifyItemChanged(index, "selection_change")
            }
        }
    }

    override fun onBindViewHolder(holder: MyBagViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads.contains("selection_change")) {
            // 선택 상태만 업데이트
            holder.updateSelectionState(getItem(position))
        } else {
            // 전체 바인딩
            holder.bind(getItem(position))
        }
    }

    inner class MyBagViewHolder(private val binding: ItemMyBagBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                // 상품 정보 바인딩
                tvProductTitle.text = cartItem.title
                tvProductPrice.text = root.context.getString(R.string.product_price_format, String.format("%,d", cartItem.price))
                tvQuantity.text = cartItem.quantity.toString()

                // 이미지 로드
                Glide.with(root.context)
                    .load(cartItem.thumbnail)
                    .centerCrop()
                    .into(ivProductImage)

                // 총 가격 계산
                val totalPrice = cartItem.price * cartItem.quantity
                tvTotalPrice.text = root.context.getString(R.string.my_bag_total_format, String.format("%,d", totalPrice))

                // 선택 상태 업데이트
                updateSelectionState(cartItem)

                // 클릭 리스너 설정
                setupClickListeners(cartItem)
            }
        }

        fun updateSelectionState(cartItem: CartItem) {
            binding.cbSelect.setOnCheckedChangeListener(null) // 리스너 제거
            binding.cbSelect.isChecked = selectedItems.contains(cartItem.productId)

            // 리스너 재설정
            binding.cbSelect.setOnCheckedChangeListener { _, _ ->
                onSelectionChanged(cartItem.productId)
            }
        }

        private fun setupClickListeners(cartItem: CartItem) {
            binding.apply {
                // 체크박스 클릭 리스너
                cbSelect.setOnCheckedChangeListener { _, _ ->
                    onSelectionChanged(cartItem.productId)
                }

                // 수량 증가 버튼
                btnIncrease.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    if (newQuantity <= 99) { // 최대 수량 제한
                        onQuantityChanged(cartItem, newQuantity)
                    }
                }

                // 수량 감소 버튼
                btnDecrease.setOnClickListener {
                    val newQuantity = cartItem.quantity - 1
                    if (newQuantity >= 1) {
                        onQuantityChanged(cartItem, newQuantity)
                    }
                }

                // 삭제 버튼
                btnDelete.setOnClickListener {
                    onDeleteClicked(cartItem)
                }
            }
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}