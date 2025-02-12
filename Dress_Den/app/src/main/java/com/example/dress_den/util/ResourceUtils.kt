package com.example.dress_den.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object ResourceUtils {
    
    // String Resources
    fun getString(context: Context, @StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun getString(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    fun getStringArray(context: Context, @ArrayRes resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }

    // Color Resources
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    fun getColorStateList(context: Context, @ColorRes colorRes: Int): ColorStateList? {
        return ContextCompat.getColorStateList(context, colorRes)
    }

    // Dimension Resources
    fun getDimension(context: Context, @DimenRes dimenRes: Int): Float {
        return context.resources.getDimension(dimenRes)
    }

    fun getDimensionPixelSize(context: Context, @DimenRes dimenRes: Int): Int {
        return context.resources.getDimensionPixelSize(dimenRes)
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun pxToDp(px: Int): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun spToPx(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    // Drawable Resources
    fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawableRes)
    }

    fun getDrawableForDensity(
        context: Context,
        @DrawableRes drawableRes: Int,
        density: Int
    ): Drawable? {
        return context.resources.getDrawableForDensity(drawableRes, density, context.theme)
    }

    // Font Resources
    fun getFont(context: Context, @FontRes fontRes: Int): Typeface? {
        return ResourcesCompat.getFont(context, fontRes)
    }

    // Raw Resources
    fun readRawResource(context: Context, @RawRes rawRes: Int): String {
        val inputStream = context.resources.openRawResource(rawRes)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        
        return stringBuilder.toString()
    }

    // Asset Resources
    fun readAssetFile(context: Context, fileName: String): String {
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        
        return stringBuilder.toString()
    }

    // Configuration
    fun isNightMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun getScreenDensity(): Float {
        return Resources.getSystem().displayMetrics.density
    }

    fun getLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }

    // Resource Qualifiers
    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and 
               Configuration.SCREENLAYOUT_SIZE_MASK >= 
               Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun getScreenOrientation(context: Context): Int {
        return context.resources.configuration.orientation
    }

    fun isLandscape(context: Context): Boolean {
        return getScreenOrientation(context) == Configuration.ORIENTATION_LANDSCAPE
    }

    // Resource IDs
    fun getResourceId(context: Context, name: String, type: String): Int {
        return context.resources.getIdentifier(name, type, context.packageName)
    }

    fun getResourceName(context: Context, @AnyRes resId: Int): String {
        return context.resources.getResourceName(resId)
    }

    // Theme Attributes
    fun getThemeColor(context: Context, @AttrRes attributeId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attributeId, typedValue, true)
        return typedValue.data
    }

    fun getThemeDrawable(context: Context, @AttrRes attributeId: Int): Drawable? {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attributeId, typedValue, true)
        return ContextCompat.getDrawable(context, typedValue.resourceId)
    }

    // Resource Loading Errors
    class ResourceNotFoundException(message: String) : Exception(message)
}
