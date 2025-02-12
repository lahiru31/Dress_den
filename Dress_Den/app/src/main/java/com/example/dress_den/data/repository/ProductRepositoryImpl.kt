package com.example.dress_den.data.repository

import com.example.dress_den.data.local.preferences.UserPreferences
import com.example.dress_den.data.remote.Resource
import com.example.dress_den.data.remote.api.DressDenApiService
import com.example.dress_den.data.remote.dto.ProductReviewRequest
import com.example.dress_den.data.remote.safeApiCall
import com.example.dress_den.domain.model.Product
import com.example.dress_den.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: DressDenApiService,
    private val userPreferences: UserPreferences
) : ProductRepository {

    override suspend fun getProducts(
        page: Int,
        limit: Int,
        categoryId: String?,
        query: String?,
        sort: String?
    ): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProducts(page, limit, categoryId, query, sort)
        }

        when (response) {
            is Resource.Success -> {
                val products = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(products))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getProductById(productId: String): Flow<Resource<Product>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProductById(productId)
        }

        when (response) {
            is Resource.Success -> {
                response.data.data?.let { productDto ->
                    emit(Resource.Success(productDto.toDomainModel()))
                } ?: emit(Resource.Error("Product not found"))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getFeaturedProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProducts(
                page = 1,
                limit = 10,
                sort = "featured"
            )
        }

        when (response) {
            is Resource.Success -> {
                val products = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(products))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getRecommendedProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProducts(
                page = 1,
                limit = 10,
                sort = "recommended"
            )
        }

        when (response) {
            is Resource.Success -> {
                val products = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(products))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun searchProducts(
        query: String,
        filters: Map<String, Any>?,
        sort: String?,
        page: Int,
        limit: Int
    ): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProducts(
                page = page,
                limit = limit,
                query = query,
                sort = sort
            )
        }

        when (response) {
            is Resource.Success -> {
                val products = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(products))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getProductsByCategory(
        categoryId: String,
        page: Int,
        limit: Int,
        sort: String?
    ): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProducts(
                page = page,
                limit = limit,
                categoryId = categoryId,
                sort = sort
            )
        }

        when (response) {
            is Resource.Success -> {
                val products = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(products))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getRelatedProducts(
        productId: String,
        limit: Int
    ): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        
        val response = safeApiCall {
            api.getProducts(
                page = 1,
                limit = limit,
                sort = "related:$productId"
            )
        }

        when (response) {
            is Resource.Success -> {
                val products = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(products))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getProductReviews(
        productId: String,
        page: Int,
        limit: Int
    ): Flow<Resource<List<ProductRepository.Product.Review>>> = flow {
        // Implementation for getting product reviews
        // This would typically involve a separate API endpoint for reviews
        emit(Resource.Loading)
    }

    override suspend fun addProductReview(
        productId: String,
        rating: Float,
        comment: String,
        images: List<String>?
    ): Flow<Resource<ProductRepository.Product.Review>> = flow {
        emit(Resource.Loading)
        
        val token = userPreferences.getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val request = ProductReviewRequest(rating, comment, images)
        // Implementation for adding a product review
        // This would typically involve a POST request to a reviews endpoint
        emit(Resource.Loading)
    }

    override suspend fun updateProductReview(
        productId: String,
        reviewId: String,
        rating: Float,
        comment: String,
        images: List<String>?
    ): Flow<Resource<ProductRepository.Product.Review>> = flow {
        emit(Resource.Loading)
        
        val token = userPreferences.getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val request = ProductReviewRequest(rating, comment, images)
        // Implementation for updating a product review
        // This would typically involve a PUT request to a reviews endpoint
        emit(Resource.Loading)
    }

    override suspend fun deleteProductReview(
        productId: String,
        reviewId: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        val token = userPreferences.getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        // Implementation for deleting a product review
        // This would typically involve a DELETE request to a reviews endpoint
        emit(Resource.Loading)
    }

    private fun com.example.dress_den.data.remote.dto.ProductDto.toDomainModel(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price.toBigDecimal(),
            discountPrice = discountPrice?.toBigDecimal(),
            imageUrls = imageUrls,
            category = Category("", "", "", ""), // This should be properly mapped
            sizes = sizes.map { Product.Size(it.id, it.name, it.measurement) },
            colors = colors.map { Product.Color(it.id, it.name, it.hexCode) },
            brand = brand,
            rating = rating,
            reviewCount = reviewCount,
            stockQuantity = stockQuantity,
            isWishlisted = isWishlisted,
            tags = tags,
            specifications = specifications
        )
    }
}
