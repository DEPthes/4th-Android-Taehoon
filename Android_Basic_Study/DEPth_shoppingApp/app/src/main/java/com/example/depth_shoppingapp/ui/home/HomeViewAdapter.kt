package com.example.depth_shoppingapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.depth_shoppingapp.R
import com.example.depth_shoppingapp.product.ProductDTO

class HomeViewAdapter(
    private val onItemClick: (ProductDTO) -> Unit
) : RecyclerView.Adapter<HomeViewAdapter.HomeViewHolder>() {

    private val products = mutableListOf<ProductDTO>()

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productThumbnail: ImageView = itemView.findViewById(R.id.iv_product_thumbnail)
        val productTitle: TextView = itemView.findViewById(R.id.tv_product_title)
        val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        val productRating: TextView = itemView.findViewById(R.id.tv_product_rating)
        val productScore: TextView = itemView.findViewById(R.id.tv_product_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_search_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val product = products[position]

        // 상품 정보 바인딩
        holder.productTitle.text = product.title
        holder.productPrice.text = "₩${String.format("%,d", product.price)}"
        holder.productRating.text = "★"
        holder.productScore.text = product.rating.toString()

        // 썸네일 이미지 로드 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(product.thumbnail)
            .placeholder(R.drawable.ic_placeholder) // 플레이스홀더 이미지 필요
            .error(R.drawable.ic_error) // 에러 이미지 필요
            .into(holder.productThumbnail)

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // 상품 목록 업데이트
    fun updateProducts(newProducts: List<ProductDTO>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    // 상품 추가 (무한스크롤용)
    fun addProducts(newProducts: List<ProductDTO>) {
        val startPosition = products.size
        products.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }

    // 현재 상품 개수
    fun getCurrentItemCount(): Int {
        return products.size
    }
}