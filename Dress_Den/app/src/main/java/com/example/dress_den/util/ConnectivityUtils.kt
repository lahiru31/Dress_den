package com.example.dress_den.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

object ConnectivityUtils {

    fun observeConnectivity(context: Context): Flow<ConnectionState> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(getConnectionState(connectivityManager))
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectionState.Unavailable)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                trySend(getConnectionState(connectivityManager))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send initial state
        trySend(getConnectionState(connectivityManager))

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    fun getConnectionState(context: Context): ConnectionState {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getConnectionState(connectivityManager)
    }

    private fun getConnectionState(connectivityManager: ConnectivityManager): ConnectionState {
        val network = connectivityManager.activeNetwork ?: return ConnectionState.Unavailable
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionState.Unavailable

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                ConnectionState.Available(
                    ConnectionType.WIFI,
                    isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED),
                    strengthInfo = getWifiStrengthInfo(capabilities)
                )
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                ConnectionState.Available(
                    ConnectionType.CELLULAR,
                    isMetered = true,
                    strengthInfo = getCellularStrengthInfo(capabilities)
                )
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                ConnectionState.Available(
                    ConnectionType.ETHERNET,
                    isMetered = false,
                    strengthInfo = null
                )
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                ConnectionState.Available(
                    ConnectionType.VPN,
                    isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED),
                    strengthInfo = null
                )
            }
            else -> ConnectionState.Unavailable
        }
    }

    private fun getWifiStrengthInfo(capabilities: NetworkCapabilities): ConnectionStrengthInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val signalStrength = capabilities.signalStrength
            if (signalStrength != NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED) {
                return ConnectionStrengthInfo(
                    strength = signalStrength,
                    maxStrength = NetworkCapabilities.SIGNAL_STRENGTH_GREAT
                )
            }
        }
        return null
    }

    private fun getCellularStrengthInfo(capabilities: NetworkCapabilities): ConnectionStrengthInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val signalStrength = capabilities.signalStrength
            if (signalStrength != NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED) {
                return ConnectionStrengthInfo(
                    strength = signalStrength,
                    maxStrength = NetworkCapabilities.SIGNAL_STRENGTH_GREAT
                )
            }
        }
        return null
    }

    fun isNetworkAvailable(context: Context): Boolean {
        return getConnectionState(context) is ConnectionState.Available
    }

    fun isHighSpeedConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ||
               capabilities.linkDownstreamBandwidthKbps >= 1500
    }

    fun isWifiConnection(context: Context): Boolean {
        val state = getConnectionState(context)
        return state is ConnectionState.Available && state.type == ConnectionType.WIFI
    }

    fun isCellularConnection(context: Context): Boolean {
        val state = getConnectionState(context)
        return state is ConnectionState.Available && state.type == ConnectionType.CELLULAR
    }

    sealed class ConnectionState {
        data class Available(
            val type: ConnectionType,
            val isMetered: Boolean,
            val strengthInfo: ConnectionStrengthInfo?
        ) : ConnectionState()
        
        object Unavailable : ConnectionState()
    }

    enum class ConnectionType {
        WIFI,
        CELLULAR,
        ETHERNET,
        VPN
    }

    data class ConnectionStrengthInfo(
        val strength: Int,
        val maxStrength: Int
    ) {
        fun getStrengthPercentage(): Float {
            return strength.toFloat() / maxStrength.toFloat() * 100f
        }

        fun getQualityLevel(): ConnectionQuality {
            val percentage = getStrengthPercentage()
            return when {
                percentage >= 75f -> ConnectionQuality.EXCELLENT
                percentage >= 50f -> ConnectionQuality.GOOD
                percentage >= 25f -> ConnectionQuality.FAIR
                else -> ConnectionQuality.POOR
            }
        }
    }

    enum class ConnectionQuality {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR
    }

    class NetworkException(message: String) : Exception(message)
}
