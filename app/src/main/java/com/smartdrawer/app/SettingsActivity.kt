package com.smartdrawer.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.smartdrawer.app.databinding.ActivitySettingsBinding
import com.smartdrawer.app.service.FloatingWidgetService
import com.smartdrawer.app.utils.PreferenceManager

/**
 * Settings Activity - Manages app preferences and configuration
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferenceManager = PreferenceManager(this)
        setupUI()
        loadPreferences()
    }

    private fun setupUI() {
        binding.apply {
            // Widget settings
            switchWidgetEnabled.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setWidgetEnabled(isChecked)
                if (isChecked) {
                    startFloatingService()
                } else {
                    stopFloatingService()
                }
            }

            switchGestureEnabled.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setGestureEnabled(isChecked)
                // TODO: Enable/disable gesture detection
            }

            switchAutoStart.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setAutoStartEnabled(isChecked)
            }

            // Appearance settings
            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setDarkModeEnabled(isChecked)
                applyDarkMode(isChecked)
            }

            switchGridLayout.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setGridLayoutEnabled(isChecked)
            }
        }
    }

    private fun loadPreferences() {
        binding.apply {
            switchWidgetEnabled.isChecked = preferenceManager.isWidgetEnabled()
            switchGestureEnabled.isChecked = preferenceManager.isGestureEnabled()
            switchAutoStart.isChecked = preferenceManager.isAutoStartEnabled()
            switchDarkMode.isChecked = preferenceManager.isDarkModeEnabled()
            switchGridLayout.isChecked = preferenceManager.isGridLayoutEnabled()
        }
    }

    private fun startFloatingService() {
        val serviceIntent = Intent(this, FloatingWidgetService::class.java)
        startForegroundService(serviceIntent)
    }

    private fun stopFloatingService() {
        val serviceIntent = Intent(this, FloatingWidgetService::class.java)
        stopService(serviceIntent)
    }

    private fun applyDarkMode(enabled: Boolean) {
        val mode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 