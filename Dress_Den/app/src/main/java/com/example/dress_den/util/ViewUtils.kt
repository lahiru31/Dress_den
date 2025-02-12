package com.example.dress_den.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

object ViewUtils {

    // View Visibility
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.INVISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.toggleVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    // Animations
    fun View.fadeIn(duration: Long = 300) {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(null)
    }

    fun View.fadeOut(duration: Long = 300) {
        animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
    }

    fun View.slideUp(duration: Long = 300) {
        visibility = View.VISIBLE
        animate()
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun View.slideDown(duration: Long = 300) {
        animate()
            .translationY(height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
    }

    fun View.startAnimation(@AnimRes animResId: Int) {
        val animation = AnimationUtils.loadAnimation(context, animResId)
        startAnimation(animation)
    }

    // Keyboard Management
    fun View.showKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    // Snackbar
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

    // Image Loading
    fun ImageView.loadImage(
        url: String?,
        @DrawableRes placeholder: Int? = null,
        @DrawableRes error: Int? = null,
        crossFade: Boolean = true
    ) {
        Glide.with(context)
            .load(url)
            .apply {
                placeholder?.let { placeholder(it) }
                error?.let { error(it) }
                if (crossFade) transition(DrawableTransitionOptions.withCrossFade())
            }
            .into(this)
    }

    // View Properties
    fun View.setBackgroundTint(@ColorRes colorRes: Int) {
        backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
    }

    fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
        setTextColor(ContextCompat.getColor(context, colorRes))
    }

    fun View.setBackgroundDrawableRes(@DrawableRes drawableRes: Int) {
        background = ContextCompat.getDrawable(context, drawableRes)
    }

    // Margin and Padding
    fun View.updateMargins(
        left: Int? = null,
        top: Int? = null,
        right: Int? = null,
        bottom: Int? = null
    ) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            left?.let { leftMargin = it }
            top?.let { topMargin = it }
            right?.let { rightMargin = it }
            bottom?.let { bottomMargin = it }
        }
    }

    fun View.updatePadding(
        left: Int? = null,
        top: Int? = null,
        right: Int? = null,
        bottom: Int? = null
    ) {
        setPadding(
            left ?: paddingLeft,
            top ?: paddingTop,
            right ?: paddingRight,
            bottom ?: paddingBottom
        )
    }

    // Window Insets
    fun View.applySystemWindowInsets(
        applyLeft: Boolean = true,
        applyTop: Boolean = true,
        applyRight: Boolean = true,
        applyBottom: Boolean = true
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = if (applyLeft) insets.left else view.paddingLeft,
                top = if (applyTop) insets.top else view.paddingTop,
                right = if (applyRight) insets.right else view.paddingRight,
                bottom = if (applyBottom) insets.bottom else view.paddingBottom
            )
            windowInsets
        }
    }

    // EditText Utils
    fun EditText.setErrorWithFocus(error: String?) {
        if (error != null) {
            requestFocus()
            showKeyboard()
        }
        (parent.parent as? TextInputLayout)?.error = error
    }

    // RecyclerView Utils
    fun RecyclerView.smoothScrollToTop() {
        smoothScrollToPosition(0)
    }

    fun RecyclerView.scrollToTop() {
        scrollToPosition(0)
    }

    // Ripple Effect
    fun View.enableRippleEffect() {
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val typedArray = context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        typedArray.recycle()
        setBackgroundResource(backgroundResource)
    }

    // View Measurement
    fun View.measureExactly(width: Int, height: Int) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        measure(widthSpec, heightSpec)
    }

    // View State
    fun View.enable() {
        isEnabled = true
        alpha = 1.0f
    }

    fun View.disable() {
        isEnabled = false
        alpha = 0.5f
    }

    // Resource Utils
    fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(this, drawableRes)
    }

    fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }
}
