package com.example.dress_den.util

import android.content.Context
import com.example.dress_den.domain.model.Order
import com.example.dress_den.domain.model.User
import com.stripe.android.PaymentConfiguration
import com.stripe.android.model.CardParams
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import java.math.BigDecimal
import java.util.*

object PaymentUtils {
    private const val STRIPE_PUBLISHABLE_KEY = "your_stripe_publishable_key"
    private const val CURRENCY_CODE = "USD"

    fun initialize(context: Context) {
        PaymentConfiguration.init(context, STRIPE_PUBLISHABLE_KEY)
    }

    sealed class PaymentResult {
        data class Success(val transactionId: String) : PaymentResult()
        data class Error(val message: String, val code: String? = null) : PaymentResult()
        object Cancelled : PaymentResult()
    }

    data class PaymentDetails(
        val amount: BigDecimal,
        val currency: String = CURRENCY_CODE,
        val description: String? = null,
        val metadata: Map<String, String> = emptyMap()
    )

    data class CardDetails(
        val number: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val cvc: String,
        val name: String? = null,
        val addressLine1: String? = null,
        val addressLine2: String? = null,
        val city: String? = null,
        val state: String? = null,
        val postalCode: String? = null,
        val country: String? = null
    ) {
        fun isValid(): Boolean {
            return isCardNumberValid(number) &&
                   isExpiryValid(expiryMonth, expiryYear) &&
                   isCvcValid(cvc)
        }
    }

    fun createPaymentMethodParams(cardDetails: CardDetails): PaymentMethodCreateParams {
        return PaymentMethodCreateParams.create(
            card = CardParams(
                number = cardDetails.number,
                expMonth = cardDetails.expiryMonth,
                expYear = cardDetails.expiryYear,
                cvc = cardDetails.cvc
            ),
            billingDetails = PaymentMethodCreateParams.BillingDetails(
                name = cardDetails.name,
                address = PaymentMethodCreateParams.BillingDetails.Address(
                    line1 = cardDetails.addressLine1,
                    line2 = cardDetails.addressLine2,
                    city = cardDetails.city,
                    state = cardDetails.state,
                    postalCode = cardDetails.postalCode,
                    country = cardDetails.country
                )
            )
        )
    }

    fun createConfirmPaymentIntentParams(
        clientSecret: String,
        paymentMethodId: String
    ): ConfirmPaymentIntentParams {
        return ConfirmPaymentIntentParams.createWithPaymentMethodId(
            paymentMethodId = paymentMethodId,
            clientSecret = clientSecret
        )
    }

    fun calculateOrderTotal(
        items: List<Order.OrderItem>,
        shippingCost: BigDecimal,
        taxRate: BigDecimal
    ): BigDecimal {
        val subtotal = items.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.price.multiply(BigDecimal(item.quantity)))
        }
        
        val tax = subtotal.multiply(taxRate)
            .divide(BigDecimal(100))
        
        return subtotal.add(tax).add(shippingCost)
    }

    fun formatCardNumber(number: String): String {
        return number.replace("\\s".toRegex(), "")
            .chunked(4)
            .joinToString(" ")
    }

    fun maskCardNumber(number: String): String {
        val last4 = number.takeLast(4)
        return "•••• •••• •••• $last4"
    }

    fun isCardNumberValid(number: String): Boolean {
        val cleanNumber = number.replace("\\s".toRegex(), "")
        if (!cleanNumber.all { it.isDigit() }) return false
        if (cleanNumber.length !in 13..19) return false
        
        // Luhn algorithm
        var sum = 0
        var alternate = false
        for (i in cleanNumber.length - 1 downTo 0) {
            var n = cleanNumber[i] - '0'
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    fun isExpiryValid(month: Int, year: Int): Boolean {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return when {
            month !in 1..12 -> false
            year < currentYear -> false
            year == currentYear && month < currentMonth -> false
            else -> true
        }
    }

    fun isCvcValid(cvc: String): Boolean {
        return cvc.length in 3..4 && cvc.all { it.isDigit() }
    }

    fun getCardType(number: String): CardType {
        val cleanNumber = number.replace("\\s".toRegex(), "")
        return when {
            cleanNumber.startsWith("4") -> CardType.VISA
            cleanNumber.startsWith("5") -> CardType.MASTERCARD
            cleanNumber.startsWith("34") || cleanNumber.startsWith("37") -> CardType.AMEX
            cleanNumber.startsWith("6") -> CardType.DISCOVER
            else -> CardType.UNKNOWN
        }
    }

    enum class CardType {
        VISA,
        MASTERCARD,
        AMEX,
        DISCOVER,
        UNKNOWN
    }

    fun createPaymentIntent(
        amount: BigDecimal,
        currency: String = CURRENCY_CODE,
        customerId: String? = null,
        metadata: Map<String, String> = emptyMap()
    ): Map<String, Any> {
        return mapOf(
            "amount" to amount.multiply(BigDecimal(100)).toLong(),
            "currency" to currency.lowercase(),
            "metadata" to metadata
        ).plus(
            customerId?.let { mapOf("customer" to it) } ?: emptyMap()
        )
    }

    fun validatePaymentMethod(paymentMethod: User.PaymentMethod): Boolean {
        return when (paymentMethod.type) {
            User.PaymentType.CREDIT_CARD,
            User.PaymentType.DEBIT_CARD -> {
                paymentMethod.cardNumber != null &&
                paymentMethod.expiryMonth != null &&
                paymentMethod.expiryYear != null &&
                isCardNumberValid(paymentMethod.cardNumber) &&
                isExpiryValid(paymentMethod.expiryMonth, paymentMethod.expiryYear)
            }
            User.PaymentType.UPI -> true // Add UPI validation logic
            User.PaymentType.NET_BANKING -> true // Add net banking validation logic
            User.PaymentType.WALLET -> true // Add wallet validation logic
        }
    }

    fun getPaymentMethodLabel(paymentMethod: User.PaymentMethod): String {
        return when (paymentMethod.type) {
            User.PaymentType.CREDIT_CARD,
            User.PaymentType.DEBIT_CARD -> {
                val cardType = paymentMethod.cardType ?: "Card"
                val last4 = paymentMethod.cardNumber?.takeLast(4) ?: "****"
                "$cardType ending in $last4"
            }
            User.PaymentType.UPI -> "UPI"
            User.PaymentType.NET_BANKING -> "Net Banking"
            User.PaymentType.WALLET -> "Wallet"
        }
    }
}
