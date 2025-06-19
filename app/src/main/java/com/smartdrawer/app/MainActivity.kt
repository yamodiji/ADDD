package com.smartdrawer.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartdrawer.app.databinding.ActivityMainBinding
import com.smartdrawer.app.service.FloatingWidgetService
import com.smartdrawer.app.utils.PermissionHelper
import com.smartdrawer.app.utils.PreferenceManager
import com.smartdrawer.app.viewmodel.MainViewModel

/**
 * Main Activity - Entry point for the Smart Drawer app
 * Handles permissions, service control, and navigation to settings
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        preferenceManager = PreferenceManager(this)
        permissionHelper = PermissionHelper(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            btnGrantPermissions.setOnClickListener {
                requestOverlayPermission()
            }

            btnToggleService.setOnClickListener {
                toggleFloatingService()
            }

            btnSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }

        updateServiceButton()
    }

    private fun observeViewModel() {
        viewModel.serviceRunning.observe(this) { isRunning ->
            updateServiceButtonText(isRunning)
        }
    }

    private fun requestOverlayPermission() {
        if (!permissionHelper.hasOverlayPermission()) {
            showPermissionDialog()
        } else {
            // Permission already granted
            binding.btnGrantPermissions.text = "✓ Permission Granted"
            binding.btnGrantPermissions.isEnabled = false
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_overlay_title))
            .setMessage(getString(R.string.permission_overlay_message))
            .setPositiveButton(getString(R.string.permission_grant)) { _, _ ->
                openOverlaySettings()
            }
            .setNegativeButton(getString(R.string.permission_cancel), null)
            .show()
    }

    private fun openOverlaySettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun toggleFloatingService() {
        if (!permissionHelper.hasOverlayPermission()) {
            requestOverlayPermission()
            return
        }

        val serviceIntent = Intent(this, FloatingWidgetService::class.java)
        
        if (viewModel.serviceRunning.value == true) {
            stopService(serviceIntent)
            viewModel.setServiceRunning(false)
        } else {
            // Use appropriate method based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            viewModel.setServiceRunning(true)
        }
    }

    private fun updateServiceButton() {
        val isRunning = FloatingWidgetService.isServiceRunning
        viewModel.setServiceRunning(isRunning)
    }

    private fun updateServiceButtonText(isRunning: Boolean) {
        binding.btnToggleService.text = if (isRunning) {
            getString(R.string.stop_service)
        } else {
            getString(R.string.start_service)
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if permission was granted while away
        if (permissionHelper.hasOverlayPermission()) {
            binding.btnGrantPermissions.text = "✓ Permission Granted"
            binding.btnGrantPermissions.isEnabled = false
        }
        updateServiceButton()
    }
} 