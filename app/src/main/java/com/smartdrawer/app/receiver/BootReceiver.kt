package com.smartdrawer.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.smartdrawer.app.service.FloatingWidgetService
import com.smartdrawer.app.utils.PreferenceManager
import com.smartdrawer.app.utils.PermissionHelper

/**
 * BootReceiver - Automatically starts the floating widget service on device boot
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                startServiceIfEnabled(context)
            }
        }
    }

    private fun startServiceIfEnabled(context: Context) {
        val preferenceManager = PreferenceManager(context)
        val permissionHelper = PermissionHelper(context)

        // Check if auto-start is enabled and permissions are granted
        if (preferenceManager.isAutoStartEnabled() && 
            preferenceManager.isWidgetEnabled() && 
            permissionHelper.hasOverlayPermission()) {
            
            val serviceIntent = Intent(context, FloatingWidgetService::class.java)
            
            // Use appropriate method based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}