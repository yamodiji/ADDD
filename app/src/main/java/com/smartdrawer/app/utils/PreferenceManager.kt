package com.smartdrawer.app.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * PreferenceManager - Handles all app preferences and settings
 */
class PreferenceManager(context: Context) {

    companion object {
        private const val PREF_NAME = "smart_drawer_prefs"
        private const val KEY_WIDGET_ENABLED = "widget_enabled"
        private const val KEY_GESTURE_ENABLED = "gesture_enabled"
        private const val KEY_AUTO_START_ENABLED = "auto_start_enabled"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_GRID_LAYOUT_ENABLED = "grid_layout_enabled"
        private const val KEY_WIDGET_X = "widget_x"
        private const val KEY_WIDGET_Y = "widget_y"
        private const val KEY_PINNED_APPS = "pinned_apps"
    }

    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Widget settings
    fun isWidgetEnabled(): Boolean = sharedPreferences.getBoolean(KEY_WIDGET_ENABLED, false)
    fun setWidgetEnabled(enabled: Boolean) = sharedPreferences.edit().putBoolean(KEY_WIDGET_ENABLED, enabled).apply()

    fun isGestureEnabled(): Boolean = sharedPreferences.getBoolean(KEY_GESTURE_ENABLED, true)
    fun setGestureEnabled(enabled: Boolean) = sharedPreferences.edit().putBoolean(KEY_GESTURE_ENABLED, enabled).apply()

    fun isAutoStartEnabled(): Boolean = sharedPreferences.getBoolean(KEY_AUTO_START_ENABLED, false)
    fun setAutoStartEnabled(enabled: Boolean) = sharedPreferences.edit().putBoolean(KEY_AUTO_START_ENABLED, enabled).apply()

    // Appearance settings
    fun isDarkModeEnabled(): Boolean = sharedPreferences.getBoolean(KEY_DARK_MODE_ENABLED, false)
    fun setDarkModeEnabled(enabled: Boolean) = sharedPreferences.edit().putBoolean(KEY_DARK_MODE_ENABLED, enabled).apply()

    fun isGridLayoutEnabled(): Boolean = sharedPreferences.getBoolean(KEY_GRID_LAYOUT_ENABLED, true)
    fun setGridLayoutEnabled(enabled: Boolean) = sharedPreferences.edit().putBoolean(KEY_GRID_LAYOUT_ENABLED, enabled).apply()

    // Widget position
    fun getWidgetX(): Float = sharedPreferences.getFloat(KEY_WIDGET_X, 100f)
    fun setWidgetX(x: Float) = sharedPreferences.edit().putFloat(KEY_WIDGET_X, x).apply()

    fun getWidgetY(): Float = sharedPreferences.getFloat(KEY_WIDGET_Y, 100f)
    fun setWidgetY(y: Float) = sharedPreferences.edit().putFloat(KEY_WIDGET_Y, y).apply()

    // Pinned apps
    fun getPinnedApps(): Set<String> = sharedPreferences.getStringSet(KEY_PINNED_APPS, emptySet()) ?: emptySet()
    fun setPinnedApps(apps: Set<String>) = sharedPreferences.edit().putStringSet(KEY_PINNED_APPS, apps).apply()

    fun addPinnedApp(packageName: String) {
        val pinnedApps = getPinnedApps().toMutableSet()
        pinnedApps.add(packageName)
        setPinnedApps(pinnedApps)
    }

    fun removePinnedApp(packageName: String) {
        val pinnedApps = getPinnedApps().toMutableSet()
        pinnedApps.remove(packageName)
        setPinnedApps(pinnedApps)
    }

    fun isPinnedApp(packageName: String): Boolean = getPinnedApps().contains(packageName)
} 