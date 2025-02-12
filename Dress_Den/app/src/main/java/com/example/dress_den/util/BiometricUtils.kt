package com.example.dress_den.util

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.dress_den.R

object BiometricUtils {

    private const val DEFAULT_TITLE = "Biometric Authentication"
    private const val DEFAULT_SUBTITLE = "Log in using your biometric credential"
    private const val DEFAULT_NEGATIVE_BUTTON = "Cancel"

    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }

    fun getAvailableBiometrics(context: Context): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NotEnrolled
            else -> BiometricStatus.NotAvailable
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (Int, String) -> Unit,
        onFailed: () -> Unit,
        title: String = DEFAULT_TITLE,
        subtitle: String = DEFAULT_SUBTITLE,
        negativeButtonText: String = DEFAULT_NEGATIVE_BUTTON,
        confirmationRequired: Boolean = true
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(confirmationRequired)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun getBiometricPromptInfo(
        context: Context,
        title: String = DEFAULT_TITLE,
        subtitle: String = DEFAULT_SUBTITLE,
        negativeButtonText: String = DEFAULT_NEGATIVE_BUTTON,
        confirmationRequired: Boolean = true
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(confirmationRequired)
            .build()
    }

    fun isBiometricHardwareAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
    }

    fun isBiometricEnrolled(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isStrongBiometricSupported(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun getErrorMessage(context: Context, errorCode: Int): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_HW_UNAVAILABLE ->
                context.getString(R.string.biometric_error_hw_unavailable)
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS ->
                context.getString(R.string.biometric_error_unable_to_process)
            BiometricPrompt.ERROR_TIMEOUT ->
                context.getString(R.string.biometric_error_timeout)
            BiometricPrompt.ERROR_NO_SPACE ->
                context.getString(R.string.biometric_error_no_space)
            BiometricPrompt.ERROR_CANCELED ->
                context.getString(R.string.biometric_error_canceled)
            BiometricPrompt.ERROR_LOCKOUT ->
                context.getString(R.string.biometric_error_lockout)
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                context.getString(R.string.biometric_error_lockout_permanent)
            BiometricPrompt.ERROR_USER_CANCELED ->
                context.getString(R.string.biometric_error_user_canceled)
            else -> context.getString(R.string.biometric_error_unknown)
        }
    }

    sealed class BiometricStatus {
        object Available : BiometricStatus()
        object NoHardware : BiometricStatus()
        object HardwareUnavailable : BiometricStatus()
        object NotEnrolled : BiometricStatus()
        object NotAvailable : BiometricStatus()
    }

    data class BiometricResult(
        val success: Boolean,
        val errorCode: Int? = null,
        val errorMessage: String? = null
    )

    interface BiometricCallback {
        fun onSuccess()
        fun onError(errorCode: Int, errorMessage: String)
        fun onFailed()
    }

    class BiometricBuilder(private val activity: FragmentActivity) {
        private var title: String = DEFAULT_TITLE
        private var subtitle: String = DEFAULT_SUBTITLE
        private var negativeButtonText: String = DEFAULT_NEGATIVE_BUTTON
        private var confirmationRequired: Boolean = true
        private var callback: BiometricCallback? = null

        fun setTitle(title: String) = apply { this.title = title }
        fun setSubtitle(subtitle: String) = apply { this.subtitle = subtitle }
        fun setNegativeButtonText(text: String) = apply { this.negativeButtonText = text }
        fun setConfirmationRequired(required: Boolean) = apply { this.confirmationRequired = required }
        fun setCallback(callback: BiometricCallback) = apply { this.callback = callback }

        fun build() {
            requireNotNull(callback) { "Callback must be set" }

            showBiometricPrompt(
                activity = activity,
                onSuccess = { callback?.onSuccess() },
                onError = { errorCode, errorMessage -> callback?.onError(errorCode, errorMessage) },
                onFailed = { callback?.onFailed() },
                title = title,
                subtitle = subtitle,
                negativeButtonText = negativeButtonText,
                confirmationRequired = confirmationRequired
            )
        }
    }
}
