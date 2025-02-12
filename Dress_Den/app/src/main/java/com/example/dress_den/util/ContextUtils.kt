package com.example.dress_den.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object ContextUtils {

    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }

    fun getString(context: Context, @StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun getString(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    fun getDrawable(context: Context, @DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(context, drawableRes)

    fun getLayoutInflater(context: Context): LayoutInflater {
        return LayoutInflater.from(context)
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun openAppInStore(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${context.packageName}")
            })
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            })
        }
    }

    fun shareText(context: Context, text: String, title: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun openUrl(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            LogUtils.e("ContextUtils", "Failed to open URL: $url", e)
        }
    }

    fun dial(context: Context, phoneNumber: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            })
        } catch (e: Exception) {
            LogUtils.e("ContextUtils", "Failed to dial number: $phoneNumber", e)
        }
    }

    fun sendEmail(context: Context, email: String, subject: String? = null, body: String? = null) {
        try {
            context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                body?.let { putExtra(Intent.EXTRA_TEXT, it) }
            })
        } catch (e: Exception) {
            LogUtils.e("ContextUtils", "Failed to send email to: $email", e)
        }
    }

    fun keepScreenOn(activity: Activity, keepOn: Boolean) {
        if (keepOn) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getDimensionPixelSize(context: Context, dimenRes: Int): Int {
        return context.resources.getDimensionPixelSize(dimenRes)
    }

    fun Fragment.requireApplicationContext(): Context {
        return requireContext().applicationContext
    }

    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        
        for (appProcess in appProcesses) {
            if (appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND 
                && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    fun isActivityFinishing(context: Context?): Boolean {
        if (context == null) return true
        return when (context) {
            is Activity -> context.isFinishing || context.isDestroyed
            else -> false
        }
    }

    fun getScreenWidth(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.heightPixels
    }

    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    class ContextException(message: String) : Exception(message)
}
