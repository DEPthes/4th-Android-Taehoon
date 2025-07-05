package com.example.depth_shoppingapp.product

data class ProductResponseDTO(
    val products: List<ApiProduct>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class ApiProduct(
    val id : Int,
    val title : String?,
    val price : Double?,
    val rating : Double?,
    val thumbnail : String?,
    val description : String?,
    val category : String?,
    val brand : String?, // nullable로 변경
    val stock : Int?
) {
    fun toProduct(): ProductDTO {
        return ProductDTO(
            id = this.id,
            title = this.title ?: "제품명 없음",
            price = this.price?.toInt() ?: 0,
            rating = this.rating ?: 0.0,
            thumbnail = this.thumbnail ?: "",
            description = this.description ?: "설명 없음",
            category = this.category ?: "카테고리 없음",
            brand = this.brand ?: "브랜드 없음", // null일 경우 기본값 설정
            stock = this.stock ?: 0,
        )
    }
}