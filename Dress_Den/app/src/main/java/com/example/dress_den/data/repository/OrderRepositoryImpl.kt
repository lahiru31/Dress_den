package com.example.dress_den.data.repository

import com.example.dress_den.data.local.preferences.UserPreferences
import com.example.dress_den.data.remote.Resource
import com.example.dress_den.data.remote.api.DressDenApiService
import com.example.dress_den.data.remote.dto.CreateOrderRequest
import com.example.dress_den.data.remote.safeApiCall
import com.example.dress_den.domain.model.Order
import com.example.dress_den.domain.model.User
import com.example.dress_den.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: DressDenApiService,
    private val preferences: UserPreferences
) : OrderRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    override suspend fun getOrders(
        page: Int,
        limit: Int,
        status: Order.OrderStatus?
    ): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading)

        val token = getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val response = safeApiCall {
            api.getOrders(
                "Bearer $token",
                page,
                limit,
                status?.name
            )
        }

        when (response) {
            is Resource.Success -> {
                val orders = response.data.data?.map { it.toDomainModel() } ?: emptyList()
                emit(Resource.Success(orders))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getOrderById(orderId: String): Flow<Resource<Order>> = flow {
        emit(Resource.Loading)

        val token = getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val response = safeApiCall {
            api.getOrderById("Bearer $token", orderId)
        }

        when (response) {
            is Resource.Success -> {
                response.data.data?.let { orderDto ->
                    emit(Resource.Success(orderDto.toDomainModel()))
                } ?: emit(Resource.Error("Order not found"))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun createOrder(
        addressId: String,
        paymentMethodId: String,
        items: List<Order.OrderItem>,
        couponCode: String?,
        notes: String?
    ): Flow<Resource<Order>> = flow {
        emit(Resource.Loading)

        val token = getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val orderItems = items.map {
            CreateOrderRequest.OrderItemRequest(
                productId = it.productId,
                quantity = it.quantity,
                selectedSize = it.selectedSize,
                selectedColor = it.selectedColor
            )
        }

        val request = CreateOrderRequest(
            addressId = addressId,
            paymentMethodId = paymentMethodId,
            items = orderItems,
            couponCode = couponCode,
            notes = notes
        )

        val response = safeApiCall {
            api.createOrder("Bearer $token", request)
        }

        when (response) {
            is Resource.Success -> {
                response.data.data?.let { orderDto ->
                    emit(Resource.Success(orderDto.toDomainModel()))
                } ?: emit(Resource.Error("Failed to create order"))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun cancelOrder(
        orderId: String,
        reason: String?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)

        val token = getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val response = safeApiCall {
            api.cancelOrder("Bearer $token", orderId, mapOf("reason" to reason))
        }

        when (response) {
            is Resource.Success -> emit(Resource.Success(true))
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun returnOrder(
        orderId: String,
        reason: String,
        items: List<OrderRepository.ReturnItem>
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)

        val token = getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val response = safeApiCall {
            api.returnOrder(
                "Bearer $token",
                orderId,
                mapOf(
                    "reason" to reason,
                    "items" to items
                )
            )
        }

        when (response) {
            is Resource.Success -> emit(Resource.Success(true))
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getOrderStatus(orderId: String): Flow<Resource<Order.OrderStatus>> = flow {
        emit(Resource.Loading)

        val token = getAuthToken() ?: run {
            emit(Resource.Error("User not authenticated"))
            return@flow
        }

        val response = safeApiCall {
            api.getOrderStatus("Bearer $token", orderId)
        }

        when (response) {
            is Resource.Success -> {
                response.data.data?.let { statusDto ->
                    emit(Resource.Success(Order.OrderStatus.valueOf(statusDto.status)))
                } ?: emit(Resource.Error("Failed to get order status"))
            }
            is Resource.Error -> emit(response)
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    // Helper methods
    private suspend fun getAuthToken(): String? {
        return preferences.getAuthToken()
    }

    private fun com.example.dress_den.data.remote.dto.OrderDto.toDomainModel(): Order {
        return Order(
            id = id,
            userId = userId,
            items = items.map { it.toDomainModel() },
            status = Order.OrderStatus.valueOf(status),
            shippingAddress = shippingAddress.toDomainModel(),
            paymentMethod = paymentMethod.toDomainModel(),
            paymentStatus = Order.PaymentStatus.valueOf(paymentStatus),
            subtotal = subtotal.toBigDecimal(),
            shippingCost = shippingCost.toBigDecimal(),
            tax = tax.toBigDecimal(),
            discount = discount.toBigDecimal(),
            total = total.toBigDecimal(),
            couponCode = couponCode,
            trackingNumber = trackingNumber,
            estimatedDeliveryDate = estimatedDeliveryDate?.let { dateFormat.parse(it) },
            notes = notes,
            createdAt = dateFormat.parse(createdAt),
            updatedAt = dateFormat.parse(updatedAt)
        )
    }

    private fun com.example.dress_den.data.remote.dto.OrderDto.OrderItemDto.toDomainModel(): Order.OrderItem {
        return Order.OrderItem(
            productId = productId,
            productName = productName,
            quantity = quantity,
            price = price.toBigDecimal(),
            selectedSize = selectedSize,
            selectedColor = selectedColor,
            imageUrl = imageUrl
        )
    }

    // Additional helper methods for other DTOs...
}
