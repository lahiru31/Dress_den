package com.example.dress_den.util

import android.animation.*
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

object AnimationUtils {
    private const val DEFAULT_DURATION = 300L
    private val fastOutSlowIn = FastOutSlowInInterpolator()

    // Fade Animations
    fun fadeIn(view: View, duration: Long = DEFAULT_DURATION) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        ViewCompat.animate(view)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .start()
    }

    fun fadeOut(view: View, duration: Long = DEFAULT_DURATION) {
        ViewCompat.animate(view)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {}
                override fun onAnimationCancel(view: View) {}
                override fun onAnimationEnd(view: View) {
                    view.visibility = View.GONE
                }
            })
            .start()
    }

    // Slide Animations
    fun slideUp(view: View, duration: Long = DEFAULT_DURATION) {
        view.visibility = View.VISIBLE
        view.translationY = view.height.toFloat()
        ViewCompat.animate(view)
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .start()
    }

    fun slideDown(view: View, duration: Long = DEFAULT_DURATION) {
        ViewCompat.animate(view)
            .translationY(view.height.toFloat())
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {}
                override fun onAnimationCancel(view: View) {}
                override fun onAnimationEnd(view: View) {
                    view.visibility = View.GONE
                }
            })
            .start()
    }

    // Scale Animations
    fun scaleIn(view: View, duration: Long = DEFAULT_DURATION) {
        view.visibility = View.VISIBLE
        view.scaleX = 0f
        view.scaleY = 0f
        ViewCompat.animate(view)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .start()
    }

    fun scaleOut(view: View, duration: Long = DEFAULT_DURATION) {
        ViewCompat.animate(view)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {}
                override fun onAnimationCancel(view: View) {}
                override fun onAnimationEnd(view: View) {
                    view.visibility = View.GONE
                }
            })
            .start()
    }

    // Rotate Animations
    fun rotate(view: View, degrees: Float, duration: Long = DEFAULT_DURATION) {
        ViewCompat.animate(view)
            .rotation(degrees)
            .setDuration(duration)
            .setInterpolator(fastOutSlowIn)
            .start()
    }

    // Shake Animation
    fun shake(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        animator.duration = 1000
        animator.start()
    }

    // Pulse Animation
    fun pulse(view: View) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleDown.duration = 310
        scaleDown.repeatCount = 1
        scaleDown.repeatMode = ObjectAnimator.REVERSE
        scaleDown.start()
    }

    // Bounce Animation
    fun bounce(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "translationY", 0f, -30f, 0f)
        animator.duration = 1000
        animator.interpolator = BounceInterpolator()
        animator.start()
    }

    // Flip Animation
    fun flip(view: View, duration: Long = DEFAULT_DURATION) {
        val animator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    // Custom Animation Builder
    class AnimationBuilder(private val view: View) {
        private var duration: Long = DEFAULT_DURATION
        private var interpolator: Interpolator = fastOutSlowIn
        private var startDelay: Long = 0
        private var alpha: Float? = null
        private var scaleX: Float? = null
        private var scaleY: Float? = null
        private var translationX: Float? = null
        private var translationY: Float? = null
        private var rotation: Float? = null

        fun setDuration(duration: Long) = apply { this.duration = duration }
        fun setInterpolator(interpolator: Interpolator) = apply { this.interpolator = interpolator }
        fun setStartDelay(delay: Long) = apply { this.startDelay = delay }
        fun alpha(value: Float) = apply { this.alpha = value }
        fun scaleX(value: Float) = apply { this.scaleX = value }
        fun scaleY(value: Float) = apply { this.scaleY = value }
        fun translationX(value: Float) = apply { this.translationX = value }
        fun translationY(value: Float) = apply { this.translationY = value }
        fun rotation(value: Float) = apply { this.rotation = value }

        fun start() {
            val animator = ViewCompat.animate(view)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setStartDelay(startDelay)

            alpha?.let { animator.alpha(it) }
            scaleX?.let { animator.scaleX(it) }
            scaleY?.let { animator.scaleY(it) }
            translationX?.let { animator.translationX(it) }
            translationY?.let { animator.translationY(it) }
            rotation?.let { animator.rotation(it) }

            animator.start()
        }
    }

    // Load Animation from XML
    fun loadAnimation(context: Context, animResId: Int): Animation {
        return android.view.animation.AnimationUtils.loadAnimation(context, animResId)
    }

    // Height Animation
    fun animateHeight(view: View, targetHeight: Int, duration: Long = DEFAULT_DURATION) {
        val prevHeight = view.height
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.addUpdateListener { animator ->
            view.layoutParams.height = animator.animatedValue as Int
            view.requestLayout()
        }
        valueAnimator.interpolator = fastOutSlowIn
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    // Width Animation
    fun animateWidth(view: View, targetWidth: Int, duration: Long = DEFAULT_DURATION) {
        val prevWidth = view.width
        val valueAnimator = ValueAnimator.ofInt(prevWidth, targetWidth)
        valueAnimator.addUpdateListener { animator ->
            view.layoutParams.width = animator.animatedValue as Int
            view.requestLayout()
        }
        valueAnimator.interpolator = fastOutSlowIn
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    class AnimationException(message: String) : Exception(message)
}
