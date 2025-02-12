package com.example.dress_den.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object AnalyticsUtils {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    // Screen Views
    fun logScreenView(screenName: String, screenClass: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    // User Actions
    object UserActions {
        fun logLogin(method: String) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                param(FirebaseAnalytics.Param.METHOD, method)
            }
        }

        fun logSignUp(method: String) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                param(FirebaseAnalytics.Param.METHOD, method)
            }
        }

        fun logProfileUpdate(updatedFields: List<String>) {
            firebaseAnalytics.logEvent("profile_update") {
                param("updated_fields", updatedFields.joinToString(","))
            }
        }
    }

    // E-commerce Events
    object EcommerceEvents {
        fun logViewProduct(
            productId: String,
            name: String,
            category: String,
            price: Double
        ) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_ID, productId)
                param(FirebaseAnalytics.Param.ITEM_NAME, name)
                param(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
                param(FirebaseAnalytics.Param.PRICE, price)
            }
        }

        fun logAddToCart(
            productId: String,
            name: String,
            category: String,
            price: Double,
            quantity: Int
        ) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                param(FirebaseAnalytics.Param.ITEM_ID, productId)
                param(FirebaseAnalytics.Param.ITEM_NAME, name)
                param(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
                param(FirebaseAnalytics.Param.PRICE, price)
                param(FirebaseAnalytics.Param.QUANTITY, quantity)
            }
        }

        fun logRemoveFromCart(
            productId: String,
            name: String,
            price: Double,
            quantity: Int
        ) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART) {
                param(FirebaseAnalytics.Param.ITEM_ID, productId)
                param(FirebaseAnalytics.Param.ITEM_NAME, name)
                param(FirebaseAnalytics.Param.PRICE, price)
                param(FirebaseAnalytics.Param.QUANTITY, quantity)
            }
        }

        fun logBeginCheckout(
            totalItems: Int,
            totalValue: Double,
            currency: String = "USD"
        ) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT) {
                param(FirebaseAnalytics.Param.QUANTITY, totalItems)
                param(FirebaseAnalytics.Param.VALUE, totalValue)
                param(FirebaseAnalytics.Param.CURRENCY, currency)
            }
        }

        fun logPurchase(
            orderId: String,
            totalValue: Double,
            tax: Double,
            shipping: Double,
            currency: String = "USD"
        ) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                param(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
                param(FirebaseAnalytics.Param.VALUE, totalValue)
                param(FirebaseAnalytics.Param.TAX, tax)
                param(FirebaseAnalytics.Param.SHIPPING, shipping)
                param(FirebaseAnalytics.Param.CURRENCY, currency)
            }
        }

        fun logAddToWishlist(
            productId: String,
            name: String,
            category: String,
            price: Double
        ) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST) {
                param(FirebaseAnalytics.Param.ITEM_ID, productId)
                param(FirebaseAnalytics.Param.ITEM_NAME, name)
                param(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
                param(FirebaseAnalytics.Param.PRICE, price)
            }
        }
    }

    // Search Events
    object SearchEvents {
        fun logSearch(searchTerm: String, resultCount: Int) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
                param(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm)
                param("result_count", resultCount)
            }
        }

        fun logSearchFilters(filters: Map<String, String>) {
            firebaseAnalytics.logEvent("search_filter") {
                filters.forEach { (key, value) ->
                    param(key, value)
                }
            }
        }
    }

    // Error Events
    object ErrorEvents {
        fun logError(
            errorCode: String,
            errorMessage: String,
            errorDetails: String? = null
        ) {
            firebaseAnalytics.logEvent("error_occurred") {
                param("error_code", errorCode)
                param("error_message", errorMessage)
                errorDetails?.let { param("error_details", it) }
            }
        }

        fun logNetworkError(
            endpoint: String,
            errorCode: Int,
            errorMessage: String
        ) {
            firebaseAnalytics.logEvent("network_error") {
                param("endpoint", endpoint)
                param("error_code", errorCode.toString())
                param("error_message", errorMessage)
            }
        }
    }

    // Custom Events
    fun logCustomEvent(
        eventName: String,
        params: Bundle? = null
    ) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    // User Properties
    fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    // Session Management
    fun startSession() {
        firebaseAnalytics.logEvent("session_start", null)
    }

    fun endSession() {
        firebaseAnalytics.logEvent("session_end", null)
    }

    // Reset
    fun resetAnalyticsData() {
        firebaseAnalytics.resetAnalyticsData()
    }

    // Utility functions
    private fun createBundle(vararg pairs: Pair<String, Any>): Bundle {
        return Bundle().apply {
            pairs.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                }
            }
        }
    }
}
