package com.example.dress_den.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

object KeyboardUtils {

    fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: View(activity)
        hideKeyboard(view)
    }

    fun hideKeyboard(fragment: Fragment) {
        fragment.view?.let { hideKeyboard(it) }
    }

    fun toggleKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun isKeyboardVisible(rootView: View): Boolean {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight > screenHeight * 0.15
    }

    fun addKeyboardVisibilityListener(
        activity: Activity,
        onKeyboardVisible: (Boolean) -> Unit
    ): ViewTreeObserver.OnGlobalLayoutListener {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            onKeyboardVisible(isKeyboardVisible(rootView))
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        return listener
    }

    fun removeKeyboardVisibilityListener(
        activity: Activity,
        listener: ViewTreeObserver.OnGlobalLayoutListener
    ) {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    fun setKeyboardVisibilityListener(
        activity: Activity,
        onKeyboardVisibilityChanged: (Boolean) -> Unit
    ) {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        var isKeyboardVisible = false

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (isKeyboardVisible != imeVisible) {
                isKeyboardVisible = imeVisible
                onKeyboardVisibilityChanged(imeVisible)
            }
            insets
        }
    }

    fun showKeyboardWithDelay(view: View, delayMillis: Long = 200) {
        view.postDelayed({
            showKeyboard(view)
        }, delayMillis)
    }

    fun hideKeyboardWithDelay(view: View, delayMillis: Long = 200) {
        view.postDelayed({
            hideKeyboard(view)
        }, delayMillis)
    }

    fun focusAndShowKeyboard(editText: EditText) {
        editText.requestFocus()
        showKeyboardWithDelay(editText)
    }

    fun clearFocusAndHideKeyboard(editText: EditText) {
        editText.clearFocus()
        hideKeyboard(editText)
    }

    fun adjustResize(window: Window) {
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    fun adjustPan(window: Window) {
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    fun adjustNothing(window: Window) {
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    fun setKeyboardStateListener(
        view: View,
        onShown: (() -> Unit)? = null,
        onHidden: (() -> Unit)? = null
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (imeVisible) {
                onShown?.invoke()
            } else {
                onHidden?.invoke()
            }
            insets
        }
    }

    fun disableKeyboardOnFocus(editText: EditText) {
        editText.setOnTouchListener { _, _ ->
            editText.showSoftInputOnFocus = false
            false
        }
    }

    fun enableKeyboardOnFocus(editText: EditText) {
        editText.setOnTouchListener { _, _ ->
            editText.showSoftInputOnFocus = true
            false
        }
    }

    fun getKeyboardHeight(rootView: View): Int {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        return rootView.height - rect.bottom
    }

    class KeyboardException(message: String) : Exception(message)
}
