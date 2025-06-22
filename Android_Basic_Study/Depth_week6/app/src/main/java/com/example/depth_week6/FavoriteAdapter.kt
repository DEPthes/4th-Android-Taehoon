package com.example.depth_week6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteAdapter(
    private val favoriteList: MutableList<Product>,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.tv_product_title)
        val priceText: TextView = itemView.findViewById(R.id.tv_product_price)
        val thumbnailImage: ImageView = itemView.findViewById(R.id.iv_product_thumbnail)
        val removeButton: Button = itemView.findViewById(R.id.btn_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = favoriteList[position]

        holder.titleText.text = product.title
        holder.priceText.text = "$${product.price}"

        // 찜 해제 버튼으로 설정
        holder.removeButton.text = "💔 찜 해제"
        holder.removeButton.setBackgroundColor(
            holder.itemView.context.getColor(android.R.color.holo_red_light)
        )

        // 이미지 로드
        Glide.with(holder.itemView.context)
            .load(product.thumbnail)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(holder.thumbnailImage)

        // 찜 해제 버튼 클릭 리스너
        holder.removeButton.setOnClickListener {
            onRemoveClick(product)
        }
    }

    override fun getItemCount(): Int = favoriteList.size

    fun updateFavorites(newFavorites: List<Product>) {
        favoriteList.clear()
        favoriteList.addAll(newFavorites)
        notifyDataSetChanged()
    }

    fun removeFavorite(product: Product) {
        val position = favoriteList.indexOf(product)
        if (position != -1) {
            favoriteList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearAll() {
        favoriteList.clear()
        notifyDataSetChanged()
    }
}