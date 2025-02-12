package com.example.dress_den.util

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import java.math.BigDecimal
import java.util.*

object BindingUtils {

    // View Binding Helper
    inline fun <T> ViewGroup.viewBinding(
        crossinline bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T
    ) = bindingInflater(LayoutInflater.from(this.context), this, false)

    // Common Binding Adapters
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visibilityInvisible")
    fun setVisibilityInvisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("android:enabled")
    fun setEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        view.alpha = if (enabled) 1.0f else 0.5f
    }

    // Image Loading Binding Adapters
    @JvmStatic
    @BindingAdapter(
        value = ["imageUrl", "placeholder", "error", "circleCrop"],
        requireAll = false
    )
    fun loadImage(
        imageView: ImageView,
        imageUrl: String?,
        @DrawableRes placeholder: Int? = null,
        @DrawableRes error: Int? = null,
        circleCrop: Boolean = false
    ) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply {
                transition(DrawableTransitionOptions.withCrossFade())
                if (circleCrop) apply(RequestOptions.circleCropTransform())
                placeholder?.let { placeholder(it) }
                error?.let { error(it) }
            }
            .into(imageView)
    }

    // Text Formatting Binding Adapters
    @JvmStatic
    @BindingAdapter("priceText")
    fun setPriceText(textView: TextView, price: BigDecimal?) {
        price?.let {
            textView.text = CurrencyUtils.formatAmount(it)
        }
    }

    @JvmStatic
    @BindingAdapter("dateText")
    fun setDateText(textView: TextView, date: Date?) {
        date?.let {
            textView.text = DateTimeUtils.formatDisplayDate(it)
        }
    }

    @JvmStatic
    @BindingAdapter("timeAgoText")
    fun setTimeAgoText(textView: TextView, date: Date?) {
        date?.let {
            textView.text = DateTimeUtils.getTimeAgo(it)
        }
    }

    // Error Handling Binding Adapters
    @JvmStatic
    @BindingAdapter("error")
    fun setError(textInputLayout: TextInputLayout, error: String?) {
        textInputLayout.error = error
        textInputLayout.isErrorEnabled = error != null
    }

    // RecyclerView Binding Adapters
    @JvmStatic
    @BindingAdapter("items")
    fun <T> setItems(recyclerView: RecyclerView, items: List<T>?) {
        (recyclerView.adapter as? BindableAdapter<T>)?.submitList(items)
    }

    // Background Binding Adapters
    @JvmStatic
    @BindingAdapter("android:background")
    fun setBackground(view: View, @DrawableRes drawableRes: Int) {
        view.background = ContextCompat.getDrawable(view.context, drawableRes)
    }

    @JvmStatic
    @BindingAdapter("backgroundTint")
    fun setBackgroundTint(view: View, color: Int) {
        view.backgroundTintList = ContextCompat.getColorStateList(view.context, color)
    }

    // Compound Drawable Binding Adapters
    @JvmStatic
    @BindingAdapter(
        value = ["drawableStart", "drawableTop", "drawableEnd", "drawableBottom"],
        requireAll = false
    )
    fun setCompoundDrawables(
        textView: TextView,
        @DrawableRes drawableStart: Int? = null,
        @DrawableRes drawableTop: Int? = null,
        @DrawableRes drawableEnd: Int? = null,
        @DrawableRes drawableBottom: Int? = null
    ) {
        val drawables = textView.compoundDrawables
        textView.setCompoundDrawablesWithIntrinsicBounds(
            getDrawableOrExisting(textView, drawableStart, drawables[0]),
            getDrawableOrExisting(textView, drawableTop, drawables[1]),
            getDrawableOrExisting(textView, drawableEnd, drawables[2]),
            getDrawableOrExisting(textView, drawableBottom, drawables[3])
        )
    }

    private fun getDrawableOrExisting(
        view: View,
        @DrawableRes drawableRes: Int?,
        existing: Drawable?
    ): Drawable? {
        return when {
            drawableRes != null -> ContextCompat.getDrawable(view.context, drawableRes)
            else -> existing
        }
    }

    // Interface for Adapters that can receive list updates
    interface BindableAdapter<T> {
        fun submitList(items: List<T>?)
    }

    // Custom View State Binding Adapters
    @JvmStatic
    @BindingAdapter("selected")
    fun setSelected(view: View, selected: Boolean) {
        view.isSelected = selected
    }

    @JvmStatic
    @BindingAdapter("activated")
    fun setActivated(view: View, activated: Boolean) {
        view.isActivated = activated
    }

    // Animation Binding Adapters
    @JvmStatic
    @BindingAdapter("animateVisibility")
    fun setAnimateVisibility(view: View, visible: Boolean) {
        if (visible) {
            view.visibility = View.VISIBLE
            view.alpha = 0f
            view.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        } else {
            view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction { view.visibility = View.GONE }
                .start()
        }
    }

    // Dimension Binding Adapters
    @JvmStatic
    @BindingAdapter("android:layout_height")
    fun setLayoutHeight(view: View, height: Float) {
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_width")
    fun setLayoutWidth(view: View, width: Float) {
        val layoutParams = view.layoutParams
        layoutParams.width = width.toInt()
        view.layoutParams = layoutParams
    }
}
