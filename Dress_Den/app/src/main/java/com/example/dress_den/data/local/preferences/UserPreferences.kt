package com.example.dress_den.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_FILENAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFS_FILENAME,
        Context.MODE_PRIVATE
    )

    // Auth Token
    fun saveAuthToken(token: String) {
        securePreferences.edit {
            putString(KEY_AUTH_TOKEN, token)
        }
    }

    fun getAuthToken(): String? {
        return securePreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        securePreferences.edit {
            remove(KEY_AUTH_TOKEN)
        }
    }

    // User ID
    fun saveUserId(userId: String) {
        securePreferences.edit {
            putString(KEY_USER_ID, userId)
        }
    }

    fun getUserId(): String? {
        return securePreferences.getString(KEY_USER_ID, null)
    }

    // User Settings
    fun saveUserSettings(settings: UserSettings) {
        preferences.edit {
            putString(KEY_USER_SETTINGS, gson.toJson(settings))
        }
    }

    fun getUserSettings(): UserSettings {
        val settingsJson = preferences.getString(KEY_USER_SETTINGS, null)
        return if (settingsJson != null) {
            gson.fromJson(settingsJson, UserSettings::class.java)
        } else {
            UserSettings() // Return default settings
        }
    }

    // App Theme
    fun setDarkMode(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_DARK_MODE, enabled)
        }
    }

    fun isDarkModeEnabled(): Boolean {
        return preferences.getBoolean(KEY_DARK_MODE, false)
    }

    // Language
    fun setLanguage(languageCode: String) {
        preferences.edit {
            putString(KEY_LANGUAGE, languageCode)
        }
    }

    fun getLanguage(): String {
        return preferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    // Notifications
    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    // First Launch
    fun isFirstLaunch(): Boolean {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        preferences.edit {
            putBoolean(KEY_FIRST_LAUNCH, false)
        }
    }

    // Clear all data
    fun clearAll() {
        preferences.edit { clear() }
        securePreferences.edit { clear() }
    }

    data class UserSettings(
        val notificationsEnabled: Boolean = true,
        val emailSubscription: Boolean = true,
        val darkModeEnabled: Boolean = false,
        val language: String = DEFAULT_LANGUAGE,
        val currency: String = DEFAULT_CURRENCY
    )

    companion object {
        private const val PREFS_FILENAME = "dress_den_preferences"
        private const val SECURE_PREFS_FILENAME = "dress_den_secure_preferences"
        
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_SETTINGS = "user_settings"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        private const val DEFAULT_LANGUAGE = "en"
        private const val DEFAULT_CURRENCY = "USD"
    }
}
