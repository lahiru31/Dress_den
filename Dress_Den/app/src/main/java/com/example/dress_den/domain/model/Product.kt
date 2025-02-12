package com.example.dress_den.domain.model

import java.math.BigDecimal

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val discountPrice: BigDecimal? = null,
    val imageUrls: List<String>,
    val category: Category,
    val sizes: List<Size>,
    val colors: List<Color>,
    val brand: String,
    val rating: Float,
    val reviewCount: Int,
    val stockQuantity: Int,
    val isWishlisted: Boolean = false,
    val tags: List<String> = emptyList(),
    val specifications: Map<String, String> = emptyMap()
) {
    data class Size(
        val id: String,
        val name: String,
        val measurement: String
    )

    data class Color(
        val id: String,
        val name: String,
        val hexCode: String
    )

    fun isInStock(): Boolean = stockQuantity > 0

    fun hasDiscount(): Boolean = discountPrice != null

    fun getDiscountPercentage(): Int? {
        return if (hasDiscount() && discountPrice != null) {
            val discount = price.subtract(discountPrice)
            val percentage = (discount.divide(price, 2, BigDecimal.ROUND_HALF_UP))
                .multiply(BigDecimal(100))
            percentage.toInt()
        } else null
    }

    fun getFinalPrice(): BigDecimal = discountPrice ?: price
}
