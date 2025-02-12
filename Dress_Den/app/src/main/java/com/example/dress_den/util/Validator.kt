package com.example.dress_den.util

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object Validator {
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            !email.isValidEmail() -> {
                ValidationResult.Error(Constants.ErrorMessages.INVALID_EMAIL)
            }
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            password.length < Constants.MIN_PASSWORD_LENGTH -> {
                ValidationResult.Error("Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters")
            }
            password.length > Constants.MAX_PASSWORD_LENGTH -> {
                ValidationResult.Error("Password cannot exceed ${Constants.MAX_PASSWORD_LENGTH} characters")
            }
            !password.isValidPassword() -> {
                ValidationResult.Error(
                    "Password must contain at least one uppercase letter, " +
                    "one lowercase letter, one number and one special character"
                )
            }
            else -> ValidationResult.Success
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            password != confirmPassword -> {
                ValidationResult.Error(Constants.ErrorMessages.PASSWORDS_DONT_MATCH)
            }
            else -> ValidationResult.Success
        }
    }

    fun validateName(name: String, fieldName: String = "Name"): ValidationResult {
        return when {
            name.isEmpty() -> {
                ValidationResult.Error("$fieldName cannot be empty")
            }
            name.length < Constants.MIN_USERNAME_LENGTH -> {
                ValidationResult.Error("$fieldName must be at least ${Constants.MIN_USERNAME_LENGTH} characters")
            }
            name.length > Constants.MAX_USERNAME_LENGTH -> {
                ValidationResult.Error("$fieldName cannot exceed ${Constants.MAX_USERNAME_LENGTH} characters")
            }
            else -> ValidationResult.Success
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            !phone.isValidPhone() -> {
                ValidationResult.Error(Constants.ErrorMessages.INVALID_PHONE)
            }
            else -> ValidationResult.Success
        }
    }

    fun validatePostalCode(postalCode: String): ValidationResult {
        return when {
            postalCode.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            !postalCode.matches(Constants.Regex.POSTAL_CODE.toRegex()) -> {
                ValidationResult.Error("Invalid postal code")
            }
            else -> ValidationResult.Success
        }
    }

    fun validateAddress(
        addressLine1: String,
        city: String,
        state: String,
        country: String,
        postalCode: String
    ): List<Pair<String, ValidationResult>> {
        return listOf(
            "Address Line 1" to when {
                addressLine1.isEmpty() -> ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
                addressLine1.length < 5 -> ValidationResult.Error("Address is too short")
                else -> ValidationResult.Success
            },
            "City" to when {
                city.isEmpty() -> ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
                else -> ValidationResult.Success
            },
            "State" to when {
                state.isEmpty() -> ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
                else -> ValidationResult.Success
            },
            "Country" to when {
                country.isEmpty() -> ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
                else -> ValidationResult.Success
            },
            "Postal Code" to validatePostalCode(postalCode)
        )
    }

    fun validateCardNumber(cardNumber: String): ValidationResult {
        return when {
            cardNumber.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            !isValidCreditCard(cardNumber) -> {
                ValidationResult.Error("Invalid card number")
            }
            else -> ValidationResult.Success
        }
    }

    fun validateExpiryDate(month: Int, year: Int): ValidationResult {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return when {
            year < currentYear -> {
                ValidationResult.Error("Card has expired")
            }
            year == currentYear && month < currentMonth -> {
                ValidationResult.Error("Card has expired")
            }
            month < 1 || month > 12 -> {
                ValidationResult.Error("Invalid month")
            }
            else -> ValidationResult.Success
        }
    }

    fun validateCVV(cvv: String): ValidationResult {
        return when {
            cvv.isEmpty() -> {
                ValidationResult.Error(Constants.ErrorMessages.EMPTY_FIELD)
            }
            cvv.length !in 3..4 -> {
                ValidationResult.Error("Invalid CVV")
            }
            !cvv.all { it.isDigit() } -> {
                ValidationResult.Error("CVV must contain only digits")
            }
            else -> ValidationResult.Success
        }
    }

    private fun isValidCreditCard(number: String): Boolean {
        if (number.isEmpty()) return false
        
        // Remove spaces and dashes
        val cleanNumber = number.replace("[ -]".toRegex(), "")
        
        // Check if all characters are digits
        if (!cleanNumber.all { it.isDigit() }) return false
        
        // Luhn algorithm
        var sum = 0
        var isEven = false
        
        for (i in cleanNumber.length - 1 downTo 0) {
            var digit = cleanNumber[i].toString().toInt()
            
            if (isEven) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }
            
            sum += digit
            isEven = !isEven
        }
        
        return sum % 10 == 0
    }

    fun validateSearchQuery(query: String): ValidationResult {
        return when {
            query.isEmpty() -> {
                ValidationResult.Error("Search query cannot be empty")
            }
            query.length < 2 -> {
                ValidationResult.Error("Search query too short")
            }
            else -> ValidationResult.Success
        }
    }

    fun validateQuantity(quantity: Int, maxQuantity: Int = Int.MAX_VALUE): ValidationResult {
        return when {
            quantity <= 0 -> {
                ValidationResult.Error("Quantity must be greater than 0")
            }
            quantity > maxQuantity -> {
                ValidationResult.Error("Quantity cannot exceed $maxQuantity")
            }
            else -> ValidationResult.Success
        }
    }
}
