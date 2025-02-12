package com.example.dress_den.domain.repository

import com.example.dress_den.data.remote.Resource
import com.example.dress_den.domain.model.Order
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface OrderRepository {
    // Order Management
    suspend fun getOrders(
        page: Int,
        limit: Int,
        status: Order.OrderStatus? = null
    ): Flow<Resource<List<Order>>>

    suspend fun getOrderById(orderId: String): Flow<Resource<Order>>

    suspend fun createOrder(
        addressId: String,
        paymentMethodId: String,
        items: List<Order.OrderItem>,
        couponCode: String? = null,
        notes: String? = null
    ): Flow<Resource<Order>>

    suspend fun cancelOrder(
        orderId: String,
        reason: String? = null
    ): Flow<Resource<Boolean>>

    suspend fun returnOrder(
        orderId: String,
        reason: String,
        items: List<ReturnItem>
    ): Flow<Resource<Boolean>>

    // Order Tracking
    suspend fun getOrderStatus(orderId: String): Flow<Resource<Order.OrderStatus>>

    suspend fun getOrderTimeline(orderId: String): Flow<Resource<List<Order.OrderTimelineItem>>>

    suspend fun trackOrder(
        orderId: String,
        trackingNumber: String
    ): Flow<Resource<OrderTracking>>

    // Payment Processing
    suspend fun processPayment(
        orderId: String,
        paymentMethodId: String,
        amount: BigDecimal
    ): Flow<Resource<PaymentResult>>

    suspend fun getPaymentStatus(
        orderId: String
    ): Flow<Resource<Order.PaymentStatus>>

    // Order Analytics
    suspend fun getOrderAnalytics(
        startDate: String,
        endDate: String
    ): Flow<Resource<OrderAnalytics>>

    // Data Classes
    data class ReturnItem(
        val orderItemId: String,
        val quantity: Int,
        val reason: String
    )

    data class OrderTracking(
        val orderId: String,
        val trackingNumber: String,
        val carrier: String,
        val status: String,
        val estimatedDeliveryDate: String?,
        val currentLocation: String?,
        val trackingHistory: List<TrackingEvent>
    ) {
        data class TrackingEvent(
            val status: String,
            val location: String,
            val timestamp: String,
            val description: String?
        )
    }

    data class PaymentResult(
        val success: Boolean,
        val transactionId: String?,
        val status: Order.PaymentStatus,
        val errorMessage: String?
    )

    data class OrderAnalytics(
        val totalOrders: Int,
        val totalRevenue: BigDecimal,
        val averageOrderValue: BigDecimal,
        val ordersByStatus: Map<Order.OrderStatus, Int>,
        val topProducts: List<TopProduct>,
        val revenueByDay: List<DailyRevenue>
    ) {
        data class TopProduct(
            val productId: String,
            val productName: String,
            val quantity: Int,
            val revenue: BigDecimal
        )

        data class DailyRevenue(
            val date: String,
            val revenue: BigDecimal,
            val orderCount: Int
        )
    }
}
