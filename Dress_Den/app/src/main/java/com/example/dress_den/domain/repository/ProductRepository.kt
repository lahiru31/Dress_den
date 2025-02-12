package com.example.dress_den.domain.repository

import com.example.dress_den.data.remote.Resource
import com.example.dress_den.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        limit: Int,
        categoryId: String? = null,
        query: String? = null,
        sort: String? = null
    ): Flow<Resource<List<Product>>>

    suspend fun getProductById(productId: String): Flow<Resource<Product>>

    suspend fun getFeaturedProducts(): Flow<Resource<List<Product>>>

    suspend fun getRecommendedProducts(): Flow<Resource<List<Product>>>

    suspend fun searchProducts(
        query: String,
        filters: Map<String, Any>? = null,
        sort: String? = null,
        page: Int,
        limit: Int
    ): Flow<Resource<List<Product>>>

    suspend fun getProductsByCategory(
        categoryId: String,
        page: Int,
        limit: Int,
        sort: String? = null
    ): Flow<Resource<List<Product>>>

    suspend fun getRelatedProducts(
        productId: String,
        limit: Int = 10
    ): Flow<Resource<List<Product>>>

    suspend fun getProductReviews(
        productId: String,
        page: Int,
        limit: Int
    ): Flow<Resource<List<Product.Review>>>

    suspend fun addProductReview(
        productId: String,
        rating: Float,
        comment: String,
        images: List<String>? = null
    ): Flow<Resource<Product.Review>>

    suspend fun updateProductReview(
        productId: String,
        reviewId: String,
        rating: Float,
        comment: String,
        images: List<String>? = null
    ): Flow<Resource<Product.Review>>

    suspend fun deleteProductReview(
        productId: String,
        reviewId: String
    ): Flow<Resource<Boolean>>

    data class Product.Review(
        val id: String,
        val userId: String,
        val userName: String,
        val rating: Float,
        val comment: String,
        val images: List<String>?,
        val createdAt: String,
        val updatedAt: String
    )
}
