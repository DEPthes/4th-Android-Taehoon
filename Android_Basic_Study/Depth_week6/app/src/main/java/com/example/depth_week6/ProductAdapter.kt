package com.example.depth_week6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: MutableList<Product>,
    private val onFavoriteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.tv_product_title)
        val priceText: TextView = itemView.findViewById(R.id.tv_product_price)
        val thumbnailImage: ImageView = itemView.findViewById(R.id.iv_product_thumbnail)
        val favoriteButton: Button = itemView.findViewById(R.id.btn_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.titleText.text = product.title
        holder.priceText.text = "$${product.price}"

        // 찜하기 버튼 상태 설정
        updateFavoriteButton(holder.favoriteButton, product.isFavorite)

        // 이미지 로드
        Glide.with(holder.itemView.context)
            .load(product.thumbnail)
            .placeholder(R.drawable.ic_placeholder) // placeholder 이미지 필요
            .error(R.drawable.ic_error) // error 이미지 필요
            .into(holder.thumbnailImage)

        // 찜하기 버튼 클릭 리스너
        holder.favoriteButton.setOnClickListener {
            product.isFavorite = !product.isFavorite
            updateFavoriteButton(holder.favoriteButton, product.isFavorite)
            onFavoriteClick(product)
        }
    }

    private fun updateFavoriteButton(button: Button, isFavorite: Boolean) {
        if (isFavorite) {
            button.text = "💖 찜 완료"
            button.setBackgroundColor(button.context.getColor(android.R.color.holo_red_light))
        } else {
            button.text = "🤍 찜하기"
            button.setBackgroundColor(button.context.getColor(android.R.color.darker_gray))
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateProducts(newProducts: List<Product>) {
        productList.clear()
        productList.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun clearProducts() {
        productList.clear()
        notifyDataSetChanged()
    }
}