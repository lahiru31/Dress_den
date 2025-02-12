package com.example.dress_den.domain.model

import java.util.Date

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
    val addresses: List<Address> = emptyList(),
    val defaultAddressId: String? = null,
    val wishlist: List<String> = emptyList(), // Product IDs
    val cart: Cart = Cart(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val createdAt: Date,
    val updatedAt: Date
) {
    data class Address(
        val id: String,
        val name: String, // e.g., "Home", "Office"
        val addressLine1: String,
        val addressLine2: String? = null,
        val city: String,
        val state: String,
        val country: String,
        val postalCode: String,
        val isDefault: Boolean = false,
        val phoneNumber: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null
    )

    data class Cart(
        val items: List<CartItem> = emptyList(),
        val couponCode: String? = null,
        val updatedAt: Date = Date()
    ) {
        data class CartItem(
            val productId: String,
            val quantity: Int,
            val selectedSize: String? = null,
            val selectedColor: String? = null
        )
    }

    data class PaymentMethod(
        val id: String,
        val type: PaymentType,
        val cardNumber: String? = null, // Last 4 digits for saved cards
        val cardType: String? = null,
        val expiryMonth: Int? = null,
        val expiryYear: Int? = null,
        val isDefault: Boolean = false
    )

    data class UserSettings(
        val notificationsEnabled: Boolean = true,
        val emailSubscription: Boolean = true,
        val darkModeEnabled: Boolean = false,
        val language: String = "en",
        val currency: String = "USD"
    )

    enum class PaymentType {
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        NET_BANKING,
        WALLET
    }

    fun getFullName(): String = "$firstName $lastName"

    fun getDefaultAddress(): Address? {
        return addresses.find { it.id == defaultAddressId }
    }

    fun getDefaultPaymentMethod(): PaymentMethod? {
        return paymentMethods.find { it.isDefault }
    }

    fun isProductWishlisted(productId: String): Boolean {
        return wishlist.contains(productId)
    }

    fun getCartItemCount(): Int {
        return cart.items.sumOf { it.quantity }
    }

    fun hasValidPaymentMethod(): Boolean {
        return paymentMethods.isNotEmpty()
    }

    fun hasShippingAddress(): Boolean {
        return addresses.isNotEmpty()
    }
}
