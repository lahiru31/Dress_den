package com.example.dress_den.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LocationUtils {
    private const val DEFAULT_ZOOM = 15f
    private const val MIN_DISTANCE = 100f // meters
    private const val ADDRESS_MAX_RESULTS = 5

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getCurrentLocation(context: Context): Result<Location> {
        if (!hasLocationPermission(context)) {
            return Result.failure(SecurityException("Location permission not granted"))
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return getLastLocation(fusedLocationClient)
    }

    private suspend fun getLastLocation(
        fusedLocationClient: FusedLocationProviderClient
    ): Result<Location> = suspendCancellableCoroutine { continuation ->
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(Result.success(location))
                } else {
                    continuation.resume(Result.failure(Exception("Location not available")))
                }
            }.addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
        } catch (e: SecurityException) {
            continuation.resume(Result.failure(e))
        }
    }

    suspend fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double
    ): Result<Address> = suspendCancellableCoroutine { continuation ->
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        continuation.resume(Result.success(addresses[0]))
                    } else {
                        continuation.resume(Result.failure(Exception("No address found")))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    continuation.resume(Result.success(addresses[0]))
                } else {
                    continuation.resume(Result.failure(Exception("No address found")))
                }
            }
        } catch (e: IOException) {
            continuation.resume(Result.failure(e))
        }
    }

    suspend fun getLocationFromAddress(
        context: Context,
        addressString: String
    ): Result<LatLng> = suspendCancellableCoroutine { continuation ->
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(addressString, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        continuation.resume(Result.success(latLng))
                    } else {
                        continuation.resume(Result.failure(Exception("Location not found")))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(addressString, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    continuation.resume(Result.success(latLng))
                } else {
                    continuation.resume(Result.failure(Exception("Location not found")))
                }
            }
        } catch (e: IOException) {
            continuation.resume(Result.failure(e))
        }
    }

    fun calculateDistance(
        startLatLng: LatLng,
        endLatLng: LatLng
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatLng.latitude,
            startLatLng.longitude,
            endLatLng.latitude,
            endLatLng.longitude,
            results
        )
        return results[0]
    }

    fun isLocationWithinRadius(
        centerLatLng: LatLng,
        targetLatLng: LatLng,
        radiusInMeters: Float
    ): Boolean {
        val distance = calculateDistance(centerLatLng, targetLatLng)
        return distance <= radiusInMeters
    }

    fun formatAddress(address: Address): String {
        val addressParts = mutableListOf<String>()

        // Add address line
        for (i in 0..address.maxAddressLineIndex) {
            address.getAddressLine(i)?.let { addressParts.add(it) }
        }

        // If no address lines are available, build from components
        if (addressParts.isEmpty()) {
            address.featureName?.let { addressParts.add(it) }
            address.subLocality?.let { addressParts.add(it) }
            address.locality?.let { addressParts.add(it) }
            address.adminArea?.let { addressParts.add(it) }
            address.postalCode?.let { addressParts.add(it) }
            address.countryName?.let { addressParts.add(it) }
        }

        return addressParts.joinToString(", ")
    }

    fun getLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateDistanceMeters(MIN_DISTANCE)
            .setGranularity(LocationRequest.GRANULARITY_PERMISSION_LEVEL)
            .setWaitForAccurateLocation(true)
            .build()
    }

    data class LocationDetails(
        val latitude: Double,
        val longitude: Double,
        val address: String,
        val city: String?,
        val state: String?,
        val country: String?,
        val postalCode: String?
    )

    suspend fun getLocationDetails(
        context: Context,
        latitude: Double,
        longitude: Double
    ): Result<LocationDetails> {
        return try {
            val addressResult = getAddressFromLocation(context, latitude, longitude)
            addressResult.map { address ->
                LocationDetails(
                    latitude = latitude,
                    longitude = longitude,
                    address = formatAddress(address),
                    city = address.locality,
                    state = address.adminArea,
                    country = address.countryName,
                    postalCode = address.postalCode
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
