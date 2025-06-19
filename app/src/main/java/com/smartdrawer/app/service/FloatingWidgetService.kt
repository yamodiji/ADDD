package com.smartdrawer.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.smartdrawer.app.R
import com.smartdrawer.app.databinding.LayoutFloatingWidgetBinding
import com.smartdrawer.app.overlay.DrawerOverlay
import com.smartdrawer.app.utils.PreferenceManager

/**
 * FloatingWidgetService - Manages the floating widget overlay
 */
class FloatingWidgetService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "smart_drawer_service"
        var isServiceRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var binding: LayoutFloatingWidgetBinding
    private lateinit var preferenceManager: PreferenceManager
    private var drawerOverlay: DrawerOverlay? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        preferenceManager = PreferenceManager(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        createNotificationChannel()
        createFloatingWidget()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        
        try {
            if (::floatingView.isInitialized) {
                windowManager.removeView(floatingView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        drawerOverlay?.dismiss()
    }

    private fun createFloatingWidget() {
        binding = LayoutFloatingWidgetBinding.inflate(LayoutInflater.from(this))
        floatingView = binding.root

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = preferenceManager.getWidgetX().toInt()
        layoutParams.y = preferenceManager.getWidgetY().toInt()

        setupWidgetListeners(layoutParams)
        
        try {
            windowManager.addView(floatingView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupWidgetListeners(layoutParams: WindowManager.LayoutParams) {
        binding.apply {
            ivSearch.setOnClickListener {
                showAppDrawer()
            }

            ivSettings.setOnClickListener {
                openSettings()
            }

            // Make widget draggable
            floatingView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = layoutParams.x
                        initialY = layoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, layoutParams)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        // Save position
                        preferenceManager.setWidgetX(layoutParams.x.toFloat())
                        preferenceManager.setWidgetY(layoutParams.y.toFloat())
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showAppDrawer() {
        if (drawerOverlay == null) {
            drawerOverlay = DrawerOverlay(this)
        }
        drawerOverlay?.show()
    }

    private fun openSettings() {
        val intent = Intent(this, com.smartdrawer.app.SettingsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.drawable.ic_search)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }
} 