package com.example.dress_den.data.remote.api

import com.example.dress_den.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DressDenApiService {
    // Product Endpoints
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("category") categoryId: String? = null,
        @Query("search") query: String? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: String
    ): Response<ApiResponse<ProductDto>>

    // Category Endpoints
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Path("id") categoryId: String
    ): Response<ApiResponse<CategoryDto>>

    // User Endpoints
    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserDto>>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserDto>>

    // Cart Endpoints
    @GET("cart")
    suspend fun getCart(
        @Header("Authorization") token: String
    ): Response<ApiResponse<CartDto>>

    @POST("cart/items")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<ApiResponse<CartDto>>

    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<ApiResponse<CartDto>>

    @DELETE("cart/items/{itemId}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<ApiResponse<CartDto>>

    // Order Endpoints
    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<OrderDto>>>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Response<ApiResponse<OrderDto>>

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Response<ApiResponse<OrderDto>>

    // Wishlist Endpoints
    @GET("wishlist")
    suspend fun getWishlist(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<ProductDto>>>

    @POST("wishlist/{productId}")
    suspend fun addToWishlist(
        @Header("Authorization") token: String,
        @Path("productId") productId: String
    ): Response<ApiResponse<MessageResponse>>

    @DELETE("wishlist/{productId}")
    suspend fun removeFromWishlist(
        @Header("Authorization") token: String,
        @Path("productId") productId: String
    ): Response<ApiResponse<MessageResponse>>

    // Address Endpoints
    @GET("addresses")
    suspend fun getAddresses(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<AddressDto>>>

    @POST("addresses")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Body request: AddAddressRequest
    ): Response<ApiResponse<AddressDto>>

    @PUT("addresses/{id}")
    suspend fun updateAddress(
        @Header("Authorization") token: String,
        @Path("id") addressId: String,
        @Body request: UpdateAddressRequest
    ): Response<ApiResponse<AddressDto>>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") addressId: String
    ): Response<ApiResponse<MessageResponse>>

    // Payment Methods
    @GET("payment-methods")
    suspend fun getPaymentMethods(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<PaymentMethodDto>>>

    @POST("payment-methods")
    suspend fun addPaymentMethod(
        @Header("Authorization") token: String,
        @Body request: AddPaymentMethodRequest
    ): Response<ApiResponse<PaymentMethodDto>>

    @DELETE("payment-methods/{id}")
    suspend fun deletePaymentMethod(
        @Header("Authorization") token: String,
        @Path("id") methodId: String
    ): Response<ApiResponse<MessageResponse>>

    companion object {
        const val BASE_URL = "https://api.dressden.com/v1/"
    }
}
