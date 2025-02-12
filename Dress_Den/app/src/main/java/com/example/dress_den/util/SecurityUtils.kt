package com.example.dress_den.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecurityUtils {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val AUTH_TAG_LENGTH = 128
    private const val MASTER_KEY_ALIAS = "dress_den_master_key"

    fun createMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setKeyGenParameterSpec(
                KeyGenParameterSpec.Builder(
                    MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_SIZE)
                    .build()
            )
            .build()
    }

    fun createEncryptedSharedPreferences(
        context: Context,
        fileName: String
    ): EncryptedSharedPreferences {
        val masterKey = createMasterKey(context)
        return EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun encrypt(data: String, alias: String): String {
        try {
            val cipher = getCipher()
            val secretKey = getOrCreateSecretKey(alias)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // Combine IV and encrypted data
            val combined = ByteArray(iv.size + encrypted.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)
            
            return Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            throw SecurityException("Error encrypting data", e)
        }
    }

    fun decrypt(encryptedData: String, alias: String): String {
        try {
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            val cipher = getCipher()
            
            // Extract IV
            val iv = ByteArray(12)
            System.arraycopy(combined, 0, iv, 0, 12)
            
            // Extract encrypted data
            val encrypted = ByteArray(combined.size - 12)
            System.arraycopy(combined, 12, encrypted, 0, encrypted.size)
            
            val secretKey = getOrCreateSecretKey(alias)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(AUTH_TAG_LENGTH, iv))
            
            return String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (e: Exception) {
            throw SecurityException("Error decrypting data", e)
        }
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION)
    }

    private fun getOrCreateSecretKey(alias: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        keyStore.getKey(alias, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    fun hashPassword(password: String, salt: String): String {
        val combined = password + salt
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.DEFAULT)
    }

    fun generateSalt(): String {
        val random = java.security.SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.DEFAULT)
    }

    fun isPasswordStrong(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isLongEnough = password.length >= 8

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar && isLongEnough
    }

    fun sanitizeInput(input: String): String {
        return input.replace(Regex("[<>\"'&]"), "_")
    }

    fun generateRandomToken(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun obfuscateEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email
        
        val username = parts[0]
        val domain = parts[1]
        
        val obfuscatedUsername = when {
            username.length <= 2 -> username
            username.length <= 5 -> "${username.take(2)}${"*".repeat(username.length - 2)}"
            else -> "${username.take(2)}${"*".repeat(3)}${username.takeLast(2)}"
        }
        
        return "$obfuscatedUsername@$domain"
    }

    fun obfuscatePhone(phone: String): String {
        return when {
            phone.length <= 4 -> phone
            else -> "${"*".repeat(phone.length - 4)}${phone.takeLast(4)}"
        }
    }

    class SecurityException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
