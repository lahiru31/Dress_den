package com.example.dress_den.data.remote

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(
        val message: String,
        val errorCode: String? = null,
        val errorDetails: Map<String, String>? = null
    ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        
        fun error(
            message: String,
            errorCode: String? = null,
            errorDetails: Map<String, String>? = null
        ): Resource<Nothing> = Error(message, errorCode, errorDetails)
        
        fun loading(): Resource<Nothing> = Loading
    }

    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> success(transform(data))
            is Error -> error(message, errorCode, errorDetails)
            is Loading -> loading()
        }
    }

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrDefault(defaultValue: T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    suspend fun onSuccess(action: suspend (T) -> Unit): Resource<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    suspend fun onError(action: suspend (String, String?, Map<String, String>?) -> Unit): Resource<T> {
        if (this is Error) {
            action(message, errorCode, errorDetails)
        }
        return this
    }

    suspend fun onLoading(action: suspend () -> Unit): Resource<T> {
        if (this is Loading) {
            action()
        }
        return this
    }
}

inline fun <T, R> Resource<T>.fold(
    onSuccess: (T) -> R,
    onError: (String, String?, Map<String, String>?) -> R,
    onLoading: () -> R
): R {
    return when (this) {
        is Resource.Success -> onSuccess(data)
        is Resource.Error -> onError(message, errorCode, errorDetails)
        is Resource.Loading -> onLoading()
    }
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Resource<T> {
    return try {
        Resource.success(apiCall())
    } catch (throwable: Throwable) {
        when (throwable) {
            is retrofit2.HttpException -> {
                Resource.error(
                    message = throwable.message ?: "An error occurred",
                    errorCode = throwable.code().toString()
                )
            }
            is java.net.SocketTimeoutException -> {
                Resource.error(
                    message = "Connection timed out",
                    errorCode = "TIMEOUT"
                )
            }
            is java.io.IOException -> {
                Resource.error(
                    message = "Network error occurred",
                    errorCode = "NETWORK"
                )
            }
            else -> {
                Resource.error(
                    message = throwable.message ?: "An unexpected error occurred",
                    errorCode = "UNKNOWN"
                )
            }
        }
    }
}
