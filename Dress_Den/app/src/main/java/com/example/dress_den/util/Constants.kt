package com.example.dress_den.util

object Constants {
    // API Constants
    const val BASE_URL = "https://api.dressden.com/v1/"
    const val API_TIMEOUT = 30L
    const val API_PAGE_SIZE = 20
    
    // Database Constants
    const val DATABASE_NAME = "dress_den_db"
    const val DATABASE_VERSION = 1
    
    // Shared Preferences Constants
    const val PREF_NAME = "dress_den_preferences"
    const val SECURE_PREF_NAME = "dress_den_secure_preferences"
    
    // Bundle Keys
    const val KEY_PRODUCT_ID = "product_id"
    const val KEY_CATEGORY_ID = "category_id"
    const val KEY_ORDER_ID = "order_id"
    const val KEY_USER_ID = "user_id"
    
    // Request Codes
    const val RC_SIGN_IN = 100
    const val RC_CAMERA = 101
    const val RC_GALLERY = 102
    const val RC_LOCATION = 103
    
    // Permissions
    const val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
    const val PERMISSION_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
    const val PERMISSION_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    
    // Firebase Constants
    const val FB_COLLECTION_USERS = "users"
    const val FB_COLLECTION_PRODUCTS = "products"
    const val FB_COLLECTION_ORDERS = "orders"
    const val FB_COLLECTION_CATEGORIES = "categories"
    
    // Notification Channels
    const val CHANNEL_ORDERS = "orders_channel"
    const val CHANNEL_PROMOTIONS = "promotions_channel"
    const val CHANNEL_GENERAL = "general_channel"
    
    // Time Constants
    const val SPLASH_DELAY = 2000L
    const val DEBOUNCE_TIME = 300L
    const val ANIMATION_DURATION = 300L
    
    // Cache Constants
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val CACHE_MAX_AGE = 7 * 24 * 60 * 60L // 7 days
    const val CACHE_MAX_STALE = 30 * 24 * 60 * 60L // 30 days
    
    // Validation Constants
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_PASSWORD_LENGTH = 20
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 30
    const val PHONE_NUMBER_LENGTH = 10
    
    // Error Messages
    object ErrorMessages {
        const val NO_INTERNET = "No internet connection"
        const val SERVER_ERROR = "Server error occurred"
        const val TIMEOUT_ERROR = "Request timed out"
        const val UNKNOWN_ERROR = "An unknown error occurred"
        const val INVALID_CREDENTIALS = "Invalid email or password"
        const val WEAK_PASSWORD = "Password is too weak"
        const val EMAIL_ALREADY_EXISTS = "Email already exists"
        const val INVALID_EMAIL = "Invalid email format"
        const val EMPTY_FIELD = "This field cannot be empty"
        const val PASSWORDS_DONT_MATCH = "Passwords don't match"
        const val INVALID_PHONE = "Invalid phone number"
    }
    
    // Success Messages
    object SuccessMessages {
        const val PROFILE_UPDATED = "Profile updated successfully"
        const val ORDER_PLACED = "Order placed successfully"
        const val ITEM_ADDED_TO_CART = "Item added to cart"
        const val ITEM_REMOVED_FROM_CART = "Item removed from cart"
        const val ADDRESS_ADDED = "Address added successfully"
        const val PAYMENT_METHOD_ADDED = "Payment method added successfully"
    }
    
    // Regular Expressions
    object Regex {
        const val EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        const val PHONE = "^[0-9]{10}$"
        const val PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$"
        const val USERNAME = "^[a-zA-Z0-9._-]{3,30}$"
        const val POSTAL_CODE = "^[0-9]{6}$"
    }
    
    // Date Formats
    object DateFormats {
        const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val DISPLAY_DATE_FORMAT = "dd MMM yyyy"
        const val DISPLAY_TIME_FORMAT = "hh:mm a"
        const val DISPLAY_DATE_TIME_FORMAT = "dd MMM yyyy, hh:mm a"
    }
}
