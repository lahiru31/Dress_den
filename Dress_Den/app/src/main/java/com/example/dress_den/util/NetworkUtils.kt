package com.example.dress_den.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun observeNetworkState(context: Context): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                 networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(isConnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Initial state
        val currentState = isNetworkAvailable(context)
        trySend(currentState)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    fun getConnectionType(context: Context): ConnectionType {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return ConnectionType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.VPN
            else -> ConnectionType.NONE
        }
    }

    fun isHighSpeedConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ||
               capabilities.linkDownstreamBandwidthKbps >= 1500
    }

    fun hasActiveInternetConnection(): Boolean {
        return try {
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            socket.connect(socketAddress, 3000)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun getNetworkInfo(context: Context): NetworkInfo {
        val connectionType = getConnectionType(context)
        val isHighSpeed = isHighSpeedConnection(context)
        val isMetered = isMeteredConnection(context)

        return NetworkInfo(
            isConnected = isNetworkAvailable(context),
            connectionType = connectionType,
            isHighSpeed = isHighSpeed,
            isMetered = isMetered
        )
    }

    private fun isMeteredConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.isActiveNetworkMetered
    }

    enum class ConnectionType {
        WIFI,
        CELLULAR,
        ETHERNET,
        VPN,
        NONE
    }

    data class NetworkInfo(
        val isConnected: Boolean,
        val connectionType: ConnectionType,
        val isHighSpeed: Boolean,
        val isMetered: Boolean
    )

    sealed class NetworkError : Exception() {
        object NoInternet : NetworkError()
        object Timeout : NetworkError()
        object ServerError : NetworkError()
        data class Unknown(override val message: String?) : NetworkError()
    }

    fun getErrorMessage(error: NetworkError): String {
        return when (error) {
            is NetworkError.NoInternet -> Constants.ErrorMessages.NO_INTERNET
            is NetworkError.Timeout -> Constants.ErrorMessages.TIMEOUT_ERROR
            is NetworkError.ServerError -> Constants.ErrorMessages.SERVER_ERROR
            is NetworkError.Unknown -> error.message ?: Constants.ErrorMessages.UNKNOWN_ERROR
        }
    }

    fun parseNetworkError(throwable: Throwable): NetworkError {
        return when (throwable) {
            is IOException -> NetworkError.NoInternet
            is retrofit2.HttpException -> {
                when (throwable.code()) {
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown(throwable.message())
                }
            }
            is java.net.SocketTimeoutException -> NetworkError.Timeout
            else -> NetworkError.Unknown(throwable.message)
        }
    }
}
