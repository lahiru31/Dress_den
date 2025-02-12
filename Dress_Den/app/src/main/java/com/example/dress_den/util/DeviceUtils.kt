package com.example.dress_den.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.util.*

object DeviceUtils {
    
    fun getDeviceInfo(context: Context): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            brand = Build.BRAND,
            device = Build.DEVICE,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT,
            screenResolution = getScreenResolution(context),
            screenDensity = context.resources.displayMetrics.densityDpi,
            deviceId = getDeviceId(context),
            isTablet = isTablet(context),
            isEmulator = isEmulator(),
            totalRam = getTotalRam(context),
            availableRam = getAvailableRam(context),
            totalStorage = getTotalStorage(),
            availableStorage = getAvailableStorage(),
            deviceLanguage = Locale.getDefault().language,
            deviceCountry = Locale.getDefault().country,
            carrierName = getCarrierName(context),
            appVersion = getAppVersion(context),
            buildNumber = getBuildNumber(context)
        )
    }

    private fun getScreenResolution(context: Context): String {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        return "${metrics.widthPixels}x${metrics.heightPixels}"
    }

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    private fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
               android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK >=
               android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                "google_sdk" == Build.PRODUCT)
    }

    private fun getTotalRam(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }

    private fun getAvailableRam(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    private fun getTotalStorage(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return stat.blockSizeLong * stat.blockCountLong
    }

    private fun getAvailableStorage(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return stat.blockSizeLong * stat.availableBlocksLong
    }

    private fun getCarrierName(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperatorName
    }

    private fun getAppVersion(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    private fun getBuildNumber(context: Context): Int {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
    }

    fun isRooted(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )

        return paths.any { File(it).exists() }
    }

    fun hasGooglePlayServices(context: Context): Boolean {
        val googlePlayStore = "com.android.vending"
        val googlePlayServices = "com.google.android.gms"
        
        return try {
            context.packageManager.getPackageInfo(googlePlayStore, 0)
            context.packageManager.getPackageInfo(googlePlayServices, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getDeviceType(): DeviceType {
        return when (Build.DEVICE) {
            "generic" -> DeviceType.EMULATOR
            else -> when {
                isTablet() -> DeviceType.TABLET
                else -> DeviceType.PHONE
            }
        }
    }

    private fun isTablet(): Boolean {
        return ((Build.BRAND.startsWith("ASUS") && !Build.MODEL.contains("Phone")) ||
                Build.BRAND.startsWith("Samsung") && Build.MODEL.contains("Tab"))
    }

    data class DeviceInfo(
        val manufacturer: String,
        val model: String,
        val brand: String,
        val device: String,
        val androidVersion: String,
        val sdkVersion: Int,
        val screenResolution: String,
        val screenDensity: Int,
        val deviceId: String,
        val isTablet: Boolean,
        val isEmulator: Boolean,
        val totalRam: Long,
        val availableRam: Long,
        val totalStorage: Long,
        val availableStorage: Long,
        val deviceLanguage: String,
        val deviceCountry: String,
        val carrierName: String,
        val appVersion: String,
        val buildNumber: Int
    )

    enum class DeviceType {
        PHONE,
        TABLET,
        EMULATOR
    }

    sealed class DeviceFeature {
        object Camera : DeviceFeature()
        object Bluetooth : DeviceFeature()
        object GPS : DeviceFeature()
        object NFC : DeviceFeature()
        object Fingerprint : DeviceFeature()
        object FaceRecognition : DeviceFeature()
    }

    fun hasFeature(context: Context, feature: DeviceFeature): Boolean {
        val packageManager = context.packageManager
        return when (feature) {
            is DeviceFeature.Camera -> packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
            is DeviceFeature.Bluetooth -> packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
            is DeviceFeature.GPS -> packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
            is DeviceFeature.NFC -> packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)
            is DeviceFeature.Fingerprint -> packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
            is DeviceFeature.FaceRecognition -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
            } else false
        }
    }
}
