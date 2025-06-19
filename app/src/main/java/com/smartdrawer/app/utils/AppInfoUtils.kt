package com.smartdrawer.app.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.smartdrawer.app.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AppInfoUtils - Utility class for loading and managing app information
 */
object AppInfoUtils {

    /**
     * Load all installed apps that can be launched
     */
    suspend fun loadInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val apps = mutableListOf<AppInfo>()

        try {
            // Get all apps with launcher intents
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

            for (resolveInfo in resolveInfoList) {
                try {
                    val activityInfo = resolveInfo.activityInfo
                    val packageName = activityInfo.packageName
                    val appName = resolveInfo.loadLabel(packageManager).toString()
                    val icon = resolveInfo.loadIcon(packageManager)
                    val isSystemApp = (activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    // Skip this app itself
                    if (packageName == context.packageName) {
                        continue
                    }

                    val appInfo = AppInfo(
                        packageName = packageName,
                        appName = appName,
                        icon = icon,
                        isSystemApp = isSystemApp
                    )

                    apps.add(appInfo)
                } catch (e: Exception) {
                    // Skip apps that cause issues
                    continue
                }
            }

            // Sort apps alphabetically
            apps.sortBy { it.appName.lowercase() }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext apps
    }

    /**
     * Filter apps based on search query
     */
    fun filterApps(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps

        val lowercaseQuery = query.lowercase()
        return apps.filter { app ->
            app.appName.lowercase().contains(lowercaseQuery) ||
            app.packageName.lowercase().contains(lowercaseQuery)
        }
    }

    /**
     * Launch an app by package name
     */
    fun launchApp(context: Context, packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if an app is installed
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
} 