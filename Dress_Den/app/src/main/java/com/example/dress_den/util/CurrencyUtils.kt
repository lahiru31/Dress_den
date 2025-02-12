package com.example.dress_den.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    private val defaultCurrency = Currency.getInstance("USD")
    private val numberFormat = NumberFormat.getCurrencyInstance().apply {
        currency = defaultCurrency
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun formatPrice(price: BigDecimal, currency: Currency = defaultCurrency): String {
        return try {
            numberFormat.apply { 
                this.currency = currency 
            }.format(price)
        } catch (e: Exception) {
            // Fallback to basic formatting if currency formatting fails
            "$${price.setScale(2, BigDecimal.ROUND_HALF_UP)}"
        }
    }

    fun formatPriceWithDiscount(
        originalPrice: BigDecimal,
        discountedPrice: BigDecimal,
        currency: Currency = defaultCurrency
    ): Pair<String, String> {
        val original = formatPrice(originalPrice, currency)
        val discounted = formatPrice(discountedPrice, currency)
        return Pair(discounted, original)
    }

    fun calculateDiscount(originalPrice: BigDecimal, discountedPrice: BigDecimal): Int {
        if (originalPrice <= BigDecimal.ZERO) return 0
        val discount = originalPrice.subtract(discountedPrice)
        return (discount.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP))
            .multiply(BigDecimal(100))
            .toInt()
    }

    fun formatDiscountPercentage(percentage: Int): String {
        return "-$percentage%"
    }

    fun parseCurrencyAmount(amount: String): BigDecimal? {
        return try {
            val cleanString = amount.replace(Regex("[^\\d.]"), "")
            BigDecimal(cleanString)
        } catch (e: Exception) {
            null
        }
    }

    fun getAvailableCurrencies(): List<Currency> {
        return Currency.getAvailableCurrencies().sortedBy { it.currencyCode }
    }

    fun convertCurrency(
        amount: BigDecimal,
        fromCurrency: Currency,
        toCurrency: Currency,
        exchangeRate: BigDecimal
    ): BigDecimal {
        return if (fromCurrency == toCurrency) {
            amount
        } else {
            amount.multiply(exchangeRate).setScale(2, BigDecimal.ROUND_HALF_UP)
        }
    }

    fun formatPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): String {
        return "${formatPrice(minPrice)} - ${formatPrice(maxPrice)}"
    }

    fun formatSavings(originalPrice: BigDecimal, discountedPrice: BigDecimal): String {
        val savings = originalPrice.subtract(discountedPrice)
        return "Save ${formatPrice(savings)}"
    }
}
