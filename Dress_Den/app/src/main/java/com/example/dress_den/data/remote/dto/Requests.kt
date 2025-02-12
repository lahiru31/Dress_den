package com.example.dress_den.data.remote.dto

data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val profileImageUrl: String?
)

data class AddToCartRequest(
    val productId: String,
    val quantity: Int,
    val selectedSize: String?,
    val selectedColor: String?
)

data class UpdateCartItemRequest(
    val quantity: Int,
    val selectedSize: String?,
    val selectedColor: String?
)

data class CreateOrderRequest(
    val addressId: String,
    val paymentMethodId: String,
    val items: List<OrderItemRequest>,
    val couponCode: String?,
    val notes: String?
) {
    data class OrderItemRequest(
        val productId: String,
        val quantity: Int,
        val selectedSize: String?,
        val selectedColor: String?
    )
}

data class AddAddressRequest(
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val phoneNumber: String?,
    val isDefault: Boolean,
    val latitude: Double?,
    val longitude: Double?
)

data class UpdateAddressRequest(
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val phoneNumber: String?,
    val isDefault: Boolean,
    val latitude: Double?,
    val longitude: Double?
)

data class AddPaymentMethodRequest(
    val type: String,
    val cardNumber: String?,
    val cardHolderName: String?,
    val expiryMonth: Int?,
    val expiryYear: Int?,
    val cvv: String?,
    val isDefault: Boolean
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?
)

data class ResetPasswordRequest(
    val email: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class UpdateUserSettingsRequest(
    val notificationsEnabled: Boolean,
    val emailSubscription: Boolean,
    val darkModeEnabled: Boolean,
    val language: String,
    val currency: String
)

data class ProductReviewRequest(
    val rating: Float,
    val comment: String,
    val images: List<String>?
)

data class ApplyCouponRequest(
    val couponCode: String
)

data class SearchRequest(
    val query: String,
    val filters: Map<String, Any>?,
    val sort: String?,
    val page: Int,
    val limit: Int
)

data class NotificationSettingsRequest(
    val orderUpdates: Boolean,
    val promotions: Boolean,
    val recommendations: Boolean,
    val emailNotifications: Boolean,
    val pushNotifications: Boolean
)

data class ContactSupportRequest(
    val subject: String,
    val message: String,
    val orderId: String?,
    val attachments: List<String>?
)

data class DeviceTokenRequest(
    val token: String,
    val deviceType: String,
    val deviceId: String
)
