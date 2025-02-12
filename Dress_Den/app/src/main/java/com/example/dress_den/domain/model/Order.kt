package com.example.dress_den.domain.model

import java.math.BigDecimal
import java.util.Date

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val shippingAddress: User.Address,
    val paymentMethod: User.PaymentMethod,
    val paymentStatus: PaymentStatus,
    val subtotal: BigDecimal,
    val shippingCost: BigDecimal,
    val tax: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal,
    val couponCode: String? = null,
    val trackingNumber: String? = null,
    val estimatedDeliveryDate: Date? = null,
    val notes: String? = null,
    val createdAt: Date,
    val updatedAt: Date
) {
    data class OrderItem(
        val productId: String,
        val productName: String,
        val quantity: Int,
        val price: BigDecimal,
        val selectedSize: String? = null,
        val selectedColor: String? = null,
        val imageUrl: String? = null
    )

    enum class OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED,
        REFUNDED
    }

    enum class PaymentStatus {
        PENDING,
        AUTHORIZED,
        PAID,
        FAILED,
        REFUNDED,
        PARTIALLY_REFUNDED
    }

    fun isActive(): Boolean {
        return status !in listOf(OrderStatus.CANCELLED, OrderStatus.RETURNED, OrderStatus.REFUNDED)
    }

    fun canBeCancelled(): Boolean {
        return status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PROCESSING)
    }

    fun canBeReturned(): Boolean {
        return status == OrderStatus.DELIVERED
    }

    fun getItemCount(): Int {
        return items.sumOf { it.quantity }
    }

    fun getStatusColor(): Int {
        return when (status) {
            OrderStatus.PENDING -> android.graphics.Color.BLUE
            OrderStatus.CONFIRMED -> android.graphics.Color.GREEN
            OrderStatus.PROCESSING -> android.graphics.Color.YELLOW
            OrderStatus.SHIPPED -> android.graphics.Color.CYAN
            OrderStatus.DELIVERED -> android.graphics.Color.GREEN
            OrderStatus.CANCELLED -> android.graphics.Color.RED
            OrderStatus.RETURNED -> android.graphics.Color.GRAY
            OrderStatus.REFUNDED -> android.graphics.Color.RED
        }
    }

    fun getFormattedTotal(): String {
        return String.format("%.2f", total)
    }

    fun getOrderTimeline(): List<OrderTimelineItem> {
        val timeline = mutableListOf<OrderTimelineItem>()
        
        timeline.add(OrderTimelineItem(
            status = OrderStatus.PENDING,
            date = createdAt,
            isCompleted = true
        ))

        when (status) {
            OrderStatus.CONFIRMED -> addTimelineItems(timeline, OrderStatus.CONFIRMED)
            OrderStatus.PROCESSING -> addTimelineItems(timeline, OrderStatus.CONFIRMED, OrderStatus.PROCESSING)
            OrderStatus.SHIPPED -> addTimelineItems(timeline, OrderStatus.CONFIRMED, OrderStatus.PROCESSING, OrderStatus.SHIPPED)
            OrderStatus.DELIVERED -> addTimelineItems(timeline, OrderStatus.CONFIRMED, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED)
            OrderStatus.CANCELLED -> {
                timeline.add(OrderTimelineItem(
                    status = OrderStatus.CANCELLED,
                    date = updatedAt,
                    isCompleted = true
                ))
            }
            else -> { /* Handle other states */ }
        }

        return timeline
    }

    private fun addTimelineItems(timeline: MutableList<OrderTimelineItem>, vararg statuses: OrderStatus) {
        statuses.forEach { status ->
            timeline.add(OrderTimelineItem(
                status = status,
                date = updatedAt,
                isCompleted = true
            ))
        }
    }

    data class OrderTimelineItem(
        val status: OrderStatus,
        val date: Date,
        val isCompleted: Boolean
    )
}
