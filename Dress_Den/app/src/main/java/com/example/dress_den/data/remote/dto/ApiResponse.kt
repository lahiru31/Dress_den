package com.example.dress_den.data.remote.dto

data class ApiResponse<T>(
    val status: Boolean,
    val message: String?,
    val data: T?,
    val error: ErrorResponse?
) {
    data class ErrorResponse(
        val code: String,
        val message: String,
        val details: Map<String, String>? = null
    )
}

data class MessageResponse(
    val message: String
)

data class ProductDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val discountPrice: Double?,
    val imageUrls: List<String>,
    val categoryId: String,
    val sizes: List<SizeDto>,
    val colors: List<ColorDto>,
    val brand: String,
    val rating: Float,
    val reviewCount: Int,
    val stockQuantity: Int,
    val isWishlisted: Boolean,
    val tags: List<String>,
    val specifications: Map<String, String>
) {
    data class SizeDto(
        val id: String,
        val name: String,
        val measurement: String
    )

    data class ColorDto(
        val id: String,
        val name: String,
        val hexCode: String
    )
}

data class CategoryDto(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val parentCategoryId: String?,
    val subCategories: List<CategoryDto>,
    val productCount: Int,
    val isActive: Boolean,
    val displayOrder: Int
)

data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val addresses: List<AddressDto>,
    val defaultAddressId: String?,
    val wishlist: List<String>,
    val cart: CartDto,
    val paymentMethods: List<PaymentMethodDto>,
    val settings: UserSettingsDto,
    val createdAt: String,
    val updatedAt: String
)

data class AddressDto(
    val id: String,
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val isDefault: Boolean,
    val phoneNumber: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class CartDto(
    val items: List<CartItemDto>,
    val couponCode: String?,
    val updatedAt: String
) {
    data class CartItemDto(
        val id: String,
        val productId: String,
        val quantity: Int,
        val selectedSize: String?,
        val selectedColor: String?
    )
}

data class PaymentMethodDto(
    val id: String,
    val type: String,
    val cardNumber: String?,
    val cardType: String?,
    val expiryMonth: Int?,
    val expiryYear: Int?,
    val isDefault: Boolean
)

data class UserSettingsDto(
    val notificationsEnabled: Boolean,
    val emailSubscription: Boolean,
    val darkModeEnabled: Boolean,
    val language: String,
    val currency: String
)

data class OrderDto(
    val id: String,
    val userId: String,
    val items: List<OrderItemDto>,
    val status: String,
    val shippingAddress: AddressDto,
    val paymentMethod: PaymentMethodDto,
    val paymentStatus: String,
    val subtotal: Double,
    val shippingCost: Double,
    val tax: Double,
    val discount: Double,
    val total: Double,
    val couponCode: String?,
    val trackingNumber: String?,
    val estimatedDeliveryDate: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
) {
    data class OrderItemDto(
        val productId: String,
        val productName: String,
        val quantity: Int,
        val price: Double,
        val selectedSize: String?,
        val selectedColor: String?,
        val imageUrl: String?
    )
}
