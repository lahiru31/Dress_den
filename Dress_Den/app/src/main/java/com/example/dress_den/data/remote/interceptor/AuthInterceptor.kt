package com.example.dress_den.data.remote.interceptor

import com.example.dress_den.data.local.preferences.UserPreferences
import com.example.dress_den.data.remote.exception.AuthenticationException
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_ACCEPT = "Accept"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val MIME_TYPE_JSON = "application/json"
        private const val TOKEN_TYPE = "Bearer"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip authentication for login, register, and public endpoints
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // Get the token from preferences
        val token = userPreferences.getAuthToken()
            ?: throw AuthenticationException("Authentication token not found")

        // Add authentication headers
        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "$TOKEN_TYPE $token")
            .header(HEADER_ACCEPT, MIME_TYPE_JSON)
            .header(HEADER_CONTENT_TYPE, MIME_TYPE_JSON)
            .build()

        val response = chain.proceed(authenticatedRequest)

        // Handle 401 Unauthorized response
        if (response.code == 401) {
            userPreferences.clearAuthToken()
            throw AuthenticationException("Session expired. Please login again.")
        }

        return response
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/auth/forgot-password") ||
               path.contains("/products") ||
               path.contains("/categories")
    }
}

class AuthenticationException(message: String) : Exception(message)
