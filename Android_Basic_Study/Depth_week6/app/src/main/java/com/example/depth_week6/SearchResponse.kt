package com.example.depth_week6

data class SearchResponse(
    val products: List<ApiProduct>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class ApiProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val thumbnail: String
) {
    fun toProduct(): Product {
        return Product(
            id = this.id,
            title = this.title,
            price = this.price.toInt(),
            thumbnail = this.thumbnail
        )
    }
}
