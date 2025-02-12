package com.example.dress_den.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import com.example.dress_den.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {

    fun showAlertDialog(
        context: Context,
        title: String? = null,
        message: String,
        positiveButton: String = context.getString(android.R.string.ok),
        negativeButton: String? = null,
        cancelable: Boolean = true,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            title?.let { setTitle(it) }
            setMessage(message)
            setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
            negativeButton?.let {
                setNegativeButton(it) { dialog, _ ->
                    dialog.dismiss()
                    onNegativeClick?.invoke()
                }
            }
            setCancelable(cancelable)
        }.show()
    }

    fun showCustomDialog(
        context: Context,
        @LayoutRes layoutResId: Int,
        cancelable: Boolean = true,
        setupView: ((View) -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(layoutResId, null)
        setupView?.invoke(view)
        
        return dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(view)
            setCancelable(cancelable)
            show()
        }
    }

    fun showBottomSheetDialog(
        context: Context,
        @LayoutRes layoutResId: Int,
        @StyleRes themeResId: Int = R.style.Theme_DressDen_BottomSheetDialog,
        cancelable: Boolean = true,
        setupView: ((View) -> Unit)? = null
    ): BottomSheetDialog {
        val dialog = BottomSheetDialog(context, themeResId)
        val view = LayoutInflater.from(context).inflate(layoutResId, null)
        setupView?.invoke(view)
        
        return dialog.apply {
            setContentView(view)
            setCancelable(cancelable)
            show()
        }
    }

    fun showLoadingDialog(
        context: Context,
        message: String? = null,
        cancelable: Boolean = false
    ): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_loading)
            setCancelable(cancelable)
            
            message?.let {
                findViewById<TextView>(R.id.loadingMessage)?.text = it
            }
            
            show()
        }
    }

    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        @DrawableRes icon: Int? = null,
        positiveButton: String = context.getString(android.R.string.ok),
        negativeButton: String = context.getString(android.R.string.cancel),
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setMessage(message)
            icon?.let { setIcon(it) }
            setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
                onConfirm()
            }
            setNegativeButton(negativeButton) { dialog, _ ->
                dialog.dismiss()
                onCancel?.invoke()
            }
        }.show()
    }

    fun showSingleChoiceDialog(
        context: Context,
        title: String,
        items: Array<String>,
        checkedItem: Int = 0,
        onItemSelected: (Int) -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setSingleChoiceItems(items, checkedItem) { dialog, which ->
                dialog.dismiss()
                onItemSelected(which)
            }
        }.show()
    }

    fun showMultiChoiceDialog(
        context: Context,
        title: String,
        items: Array<String>,
        checkedItems: BooleanArray,
        positiveButton: String = context.getString(android.R.string.ok),
        onItemsSelected: (List<Int>) -> Unit
    ): AlertDialog {
        val selectedItems = mutableListOf<Int>()
        
        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedItems.add(which)
                } else {
                    selectedItems.remove(which)
                }
            }
            setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
                onItemsSelected(selectedItems)
            }
        }.show()
    }

    fun showProgressDialog(
        context: Context,
        title: String? = null,
        message: String,
        cancelable: Boolean = false,
        onCancel: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            title?.let { setTitle(it) }
            setMessage(message)
            setCancelable(cancelable)
            if (cancelable && onCancel != null) {
                setOnCancelListener { onCancel() }
            }
            setView(R.layout.dialog_progress)
        }.show()
    }

    class DialogBuilder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var positiveButton: String? = null
        private var negativeButton: String? = null
        private var cancelable: Boolean = true
        private var icon: Int? = null
        private var onPositiveClick: (() -> Unit)? = null
        private var onNegativeClick: (() -> Unit)? = null

        fun setTitle(title: String) = apply { this.title = title }
        fun setMessage(message: String) = apply { this.message = message }
        fun setPositiveButton(text: String, onClick: () -> Unit) = apply {
            this.positiveButton = text
            this.onPositiveClick = onClick
        }
        fun setNegativeButton(text: String, onClick: () -> Unit) = apply {
            this.negativeButton = text
            this.onNegativeClick = onClick
        }
        fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun setIcon(@DrawableRes icon: Int) = apply { this.icon = icon }

        fun show(): AlertDialog {
            return MaterialAlertDialogBuilder(context).apply {
                title?.let { setTitle(it) }
                message?.let { setMessage(it) }
                icon?.let { setIcon(it) }
                positiveButton?.let { button ->
                    setPositiveButton(button) { dialog, _ ->
                        dialog.dismiss()
                        onPositiveClick?.invoke()
                    }
                }
                negativeButton?.let { button ->
                    setNegativeButton(button) { dialog, _ ->
                        dialog.dismiss()
                        onNegativeClick?.invoke()
                    }
                }
                setCancelable(cancelable)
            }.show()
        }
    }

    class DialogException(message: String) : Exception(message)
}
