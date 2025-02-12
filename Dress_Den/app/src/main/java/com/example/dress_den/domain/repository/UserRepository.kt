package com.example.dress_den.domain.repository

import com.example.dress_den.data.remote.Resource
import com.example.dress_den.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Authentication
    suspend fun login(email: String, password: String): Flow<Resource<User>>
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): Flow<Resource<User>>
    suspend fun logout(): Flow<Resource<Boolean>>
    suspend fun resetPassword(email: String): Flow<Resource<Boolean>>
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Boolean>>

    // Profile Management
    suspend fun getCurrentUser(): Flow<Resource<User>>
    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String?,
        profileImageUrl: String?
    ): Flow<Resource<User>>
    suspend fun updateUserSettings(
        notificationsEnabled: Boolean,
        emailSubscription: Boolean,
        darkModeEnabled: Boolean,
        language: String,
        currency: String
    ): Flow<Resource<User.UserSettings>>

    // Address Management
    suspend fun getAddresses(): Flow<Resource<List<User.Address>>>
    suspend fun addAddress(
        name: String,
        addressLine1: String,
        addressLine2: String?,
        city: String,
        state: String,
        country: String,
        postalCode: String,
        phoneNumber: String?,
        isDefault: Boolean,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<User.Address>>
    suspend fun updateAddress(
        addressId: String,
        name: String,
        addressLine1: String,
        addressLine2: String?,
        city: String,
        state: String,
        country: String,
        postalCode: String,
        phoneNumber: String?,
        isDefault: Boolean,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<User.Address>>
    suspend fun deleteAddress(addressId: String): Flow<Resource<Boolean>>
    suspend fun setDefaultAddress(addressId: String): Flow<Resource<Boolean>>

    // Payment Methods
    suspend fun getPaymentMethods(): Flow<Resource<List<User.PaymentMethod>>>
    suspend fun addPaymentMethod(
        type: User.PaymentType,
        cardNumber: String?,
        cardHolderName: String?,
        expiryMonth: Int?,
        expiryYear: Int?,
        cvv: String?,
        isDefault: Boolean
    ): Flow<Resource<User.PaymentMethod>>
    suspend fun deletePaymentMethod(methodId: String): Flow<Resource<Boolean>>
    suspend fun setDefaultPaymentMethod(methodId: String): Flow<Resource<Boolean>>

    // Wishlist Management
    suspend fun getWishlist(): Flow<Resource<List<String>>>
    suspend fun addToWishlist(productId: String): Flow<Resource<Boolean>>
    suspend fun removeFromWishlist(productId: String): Flow<Resource<Boolean>>
    suspend fun isProductWishlisted(productId: String): Flow<Resource<Boolean>>

    // Device Management
    suspend fun registerDeviceToken(
        token: String,
        deviceType: String,
        deviceId: String
    ): Flow<Resource<Boolean>>
    suspend fun unregisterDeviceToken(deviceId: String): Flow<Resource<Boolean>>

    // Session Management
    suspend fun isLoggedIn(): Boolean
    suspend fun getAuthToken(): String?
    suspend fun clearUserData()
}
