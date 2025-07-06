package com.example.depth_shoppingapp.ui.myBag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.depth_shoppingapp.DB.CartItem
import com.example.depth_shoppingapp.databinding.ItemMyBagBinding

class MyBagAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteClicked: (CartItem) -> Unit,
    private val onSelectionChanged: () -> Unit
) : ListAdapter<CartItem, MyBagAdapter.MyBagViewHolder>(CartItemDiffCallback()) {

    private val selectedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBagViewHolder {
        val binding = ItemMyBagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyBagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyBagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedItems(): List<CartItem> {
        return currentList.filter { selectedItems.contains(it.productId) }
    }

    fun toggleSelectAll() {
        if (selectedItems.size == currentList.size) {
            selectedItems.clear()
        } else {
            selectedItems.clear()
            selectedItems.addAll(currentList.map { it.productId })
        }
        notifyDataSetChanged()
    }

    inner class MyBagViewHolder(private val binding: ItemMyBagBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                // 상품 정보 바인딩
                tvProductTitle.text = cartItem.title
                tvProductPrice.text = "₩${String.format("%,d", cartItem.price)}"
                tvQuantity.text = cartItem.quantity.toString()

                // 이미지 로드
                Glide.with(root.context)
                    .load(cartItem.thumbnail)
                    .centerCrop()
                    .into(ivProductImage)

                // 체크박스 상태 설정
                cbSelect.isChecked = selectedItems.contains(cartItem.productId)

                // 총 가격 계산
                val totalPrice = cartItem.price * cartItem.quantity
                tvTotalPrice.text = "합계: ₩${String.format("%,d", totalPrice)}"

                // 체크박스 클릭 리스너
                cbSelect.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(cartItem.productId)
                    } else {
                        selectedItems.remove(cartItem.productId)
                    }
                    onSelectionChanged()
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