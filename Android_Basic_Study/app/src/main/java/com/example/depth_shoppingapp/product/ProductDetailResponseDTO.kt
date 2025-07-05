package com.example.depth_shoppingapp.product

data class ProductDetailResponseDTO(
    val id: Int,
    val title: String?,
    val price: Double?,
    val rating: Double?,
    val images: List<String>?,
    val description: String?,
    val category: String?,
    val brand: String?,
    val stock: Int?
) {
    fun toProductDetail(): ProductDetailDTO {
        return ProductDetailDTO(
            id = this.id,
            title = this.title ?: "제품명 없음",
            price = this.price?.toInt() ?: 0,
            rating = this.rating ?: 0.0,
            images = this.images ?: listOf(),
            description = this.description ?: "설명 없음",
            category = this.category ?: "카테고리 없음",
            brand = this.brand ?: "브랜드 없음",
            stock = this.stock ?: 0
        )
    }
}