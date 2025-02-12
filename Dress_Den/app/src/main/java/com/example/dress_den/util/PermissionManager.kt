package com.example.dress_den.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionManager(private val fragment: Fragment) {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var rationaleDialog: MaterialAlertDialogBuilder? = null
    private var settingsDialog: MaterialAlertDialogBuilder? = null

    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null

    init {
        setupPermissionLauncher()
    }

    private fun setupPermissionLauncher() {
        permissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                onPermissionGranted?.invoke()
            } else {
                if (shouldShowRationale()) {
                    showRationaleDialog()
                } else {
                    showSettingsDialog()
                }
                onPermissionDenied?.invoke()
            }
        }
    }

    fun requestPermissions(
        permissions: Array<String>,
        rationale: String,
        onGranted: () -> Unit,
        onDenied: () -> Unit = {}
    ) {
        this.onPermissionGranted = onGranted
        this.onPermissionDenied = onDenied

        when {
            hasPermissions(permissions) -> {
                onGranted()
            }
            shouldShowRationale() -> {
                setupRationaleDialog(rationale, permissions)
                showRationaleDialog()
            }
            else -> {
                permissionLauncher.launch(permissions)
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun shouldShowRationale(): Boolean {
        return fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setupRationaleDialog(rationale: String, permissions: Array<String>) {
        rationaleDialog = MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage(rationale)
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                permissionLauncher.launch(permissions)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onPermissionDenied?.invoke()
            }
    }

    private fun setupSettingsDialog() {
        settingsDialog = MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage("Required permissions have been denied. Please enable them in app settings.")
            .setPositiveButton("Settings") { dialog, _ ->
                dialog.dismiss()
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onPermissionDenied?.invoke()
            }
    }

    private fun showRationaleDialog() {
        rationaleDialog?.show()
    }

    private fun showSettingsDialog() {
        setupSettingsDialog()
        settingsDialog?.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", fragment.requireContext().packageName, null)
        }
        fragment.startActivity(intent)
    }

    companion object {
        fun hasPermission(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        // Common permission groups
        val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        val STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        val MEDIA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}

// Extension function for Fragment
fun Fragment.requestPermissions(
    permissions: Array<String>,
    rationale: String,
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
) {
    PermissionManager(this).requestPermissions(permissions, rationale, onGranted, onDenied)
}
