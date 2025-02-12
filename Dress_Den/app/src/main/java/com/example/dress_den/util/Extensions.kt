package com.example.dress_den.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Context Extensions
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

fun Context.getColorCompat(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

// View Extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String? = null,
    actionListener: ((View) -> Unit)? = null
) {
    Snackbar.make(this, message, duration).apply {
        action?.let { actionText ->
            setAction(actionText) { view ->
                actionListener?.invoke(view)
            }
        }
        show()
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

// ImageView Extensions
fun ImageView.loadImage(
    url: String?,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null,
    options: RequestOptions = RequestOptions()
) {
    Glide.with(context)
        .load(url)
        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
        }
        .into(this)
}

// EditText Extensions
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

// TextInputLayout Extensions
fun TextInputLayout.showError(message: String?) {
    error = message
    isErrorEnabled = message != null
}

// Fragment Extensions
fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(message, duration)
}

// Flow Extensions
fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect { action(it) }
    }
}

// String Extensions
fun String.isValidEmail(): Boolean {
    return matches(Constants.Regex.EMAIL.toRegex())
}

fun String.isValidPhone(): Boolean {
    return matches(Constants.Regex.PHONE.toRegex())
}

fun String.isValidPassword(): Boolean {
    return matches(Constants.Regex.PASSWORD.toRegex())
}

fun String.toDate(format: String = Constants.DateFormats.API_DATE_FORMAT): Date? {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

// Date Extensions
fun Date.format(pattern: String = Constants.DateFormats.DISPLAY_DATE_FORMAT): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

// Number Extensions
fun BigDecimal.formatAsCurrency(locale: Locale = Locale.getDefault()): String {
    return NumberFormat.getCurrencyInstance(locale).format(this)
}

fun Double.formatAsCurrency(locale: Locale = Locale.getDefault()): String {
    return NumberFormat.getCurrencyInstance(locale).format(this)
}

// Boolean Extensions
fun Boolean?.orFalse(): Boolean = this ?: false

fun Boolean?.orTrue(): Boolean = this ?: true

// Collection Extensions
fun <T> List<T>?.orEmpty(): List<T> = this ?: emptyList()

fun <K, V> Map<K, V>?.orEmpty(): Map<K, V> = this ?: emptyMap()

// Null Safety Extensions
fun <T> T?.ifNull(block: () -> T): T {
    return this ?: block()
}

fun <T> T?.ifNotNull(block: (T) -> Unit) {
    this?.let(block)
}

// Validation Extensions
fun String?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

fun String?.isNotNullOrBlank(): Boolean {
    return !this.isNullOrBlank()
}

// Time Extensions
fun Long.toReadableTime(): String {
    val hours = this / (60 * 60 * 1000)
    val minutes = (this % (60 * 60 * 1000)) / (60 * 1000)
    val seconds = (this % (60 * 1000)) / 1000
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
