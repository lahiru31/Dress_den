package com.example.dress_den.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dress_den.R

object ThemeUtils {

    fun setTheme(activity: Activity, themeResId: Int) {
        activity.setTheme(themeResId)
    }

    fun setDarkMode(mode: DarkMode) {
        val nightMode = when (mode) {
            DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    fun isDarkMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun setStatusBarColor(activity: Activity, color: Int, isLightStatusBar: Boolean = false) {
        activity.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = color
            setLightStatusBar(isLightStatusBar)
        }
    }

    fun setNavigationBarColor(activity: Activity, color: Int, isLightNavigationBar: Boolean = false) {
        activity.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            navigationBarColor = color
            setLightNavigationBar(isLightNavigationBar)
        }
    }

    fun setFullScreen(activity: Activity) {
        activity.window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    fun exitFullScreen(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    fun setTransparentStatusBar(activity: Activity) {
        activity.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    fun hideSystemBars(activity: Activity) {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun showSystemBars(activity: Activity) {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
    }

    private fun Window.setLightStatusBar(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController?.setSystemBarsAppearance(
                if (isLight) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = if (isLight) {
                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    private fun Window.setLightNavigationBar(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController?.setSystemBarsAppearance(
                if (isLight) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = if (isLight) {
                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
    }

    fun applyTheme(activity: AppCompatActivity, theme: AppTheme) {
        when (theme) {
            AppTheme.LIGHT -> {
                setDarkMode(DarkMode.LIGHT)
                setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.primary), true)
                setNavigationBarColor(activity, Color.WHITE, true)
            }
            AppTheme.DARK -> {
                setDarkMode(DarkMode.DARK)
                setStatusBarColor(activity, Color.BLACK)
                setNavigationBarColor(activity, Color.BLACK)
            }
            AppTheme.SYSTEM -> {
                setDarkMode(DarkMode.SYSTEM)
                updateSystemThemeColors(activity)
            }
        }
    }

    private fun updateSystemThemeColors(activity: Activity) {
        val isDark = isDarkMode(activity)
        if (isDark) {
            setStatusBarColor(activity, Color.BLACK)
            setNavigationBarColor(activity, Color.BLACK)
        } else {
            setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.primary), true)
            setNavigationBarColor(activity, Color.WHITE, true)
        }
    }

    enum class DarkMode {
        LIGHT,
        DARK,
        SYSTEM,
        BATTERY
    }

    enum class AppTheme {
        LIGHT,
        DARK,
        SYSTEM
    }

    sealed class ThemeAttribute(val value: Int) {
        object ColorPrimary : ThemeAttribute(android.R.attr.colorPrimary)
        object ColorPrimaryDark : ThemeAttribute(android.R.attr.colorPrimaryDark)
        object ColorAccent : ThemeAttribute(android.R.attr.colorAccent)
        object TextColorPrimary : ThemeAttribute(android.R.attr.textColorPrimary)
        object TextColorSecondary : ThemeAttribute(android.R.attr.textColorSecondary)
        object ColorBackground : ThemeAttribute(android.R.attr.colorBackground)
        object ColorSurface : ThemeAttribute(android.R.attr.colorBackground)
        object ColorError : ThemeAttribute(android.R.attr.colorError)
    }

    fun getThemeColor(context: Context, attribute: ThemeAttribute): Int {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(attribute.value))
        val color = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        return color
    }

    class ThemeException(message: String) : Exception(message)
}
