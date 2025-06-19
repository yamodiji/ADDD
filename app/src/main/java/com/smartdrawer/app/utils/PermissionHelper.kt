package com.smartdrawer.app.utils

import android.content.Context
import android.os.Build
import android.provider.Settings

/**
 * PermissionHelper - Manages app permissions
 */
class PermissionHelper(private val context: Context) {

    /**
     * Check if the app has overlay permission (System Alert Window)
     */
    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Permission not required on older versions
        }
    }

    /**
     * Check if the app can query all packages (for Android 11+)
     */
    fun hasQueryAllPackagesPermission(): Boolean {
        // This permission is declared in manifest and granted at install time
        // No runtime check needed, but we include this for completeness
        return true
    }

    /**
     * Check if all required permissions are granted
     */
    fun hasAllPermissions(): Boolean {
        return hasOverlayPermission() && hasQueryAllPackagesPermission()
    }
} 