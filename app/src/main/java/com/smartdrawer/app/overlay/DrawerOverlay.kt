package com.smartdrawer.app.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartdrawer.app.adapter.AppsAdapter
import com.smartdrawer.app.databinding.LayoutAppDrawerBinding
import com.smartdrawer.app.model.AppInfo
import com.smartdrawer.app.utils.AppInfoUtils
import com.smartdrawer.app.utils.PreferenceManager
import kotlinx.coroutines.launch

/**
 * DrawerOverlay - Manages the fullscreen app drawer overlay
 */
class DrawerOverlay(private val context: Context) {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var binding: LayoutAppDrawerBinding
    private lateinit var appsAdapter: AppsAdapter
    private lateinit var preferenceManager: PreferenceManager

    private var allApps = listOf<AppInfo>()
    private var isShowing = false

    init {
        initializeOverlay()
    }

    @SuppressLint("InflateParams")
    private fun initializeOverlay() {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        preferenceManager = PreferenceManager(context)
        
        binding = LayoutAppDrawerBinding.inflate(LayoutInflater.from(context))
        overlayView = binding.root

        setupRecyclerView()
        setupSearchBar()
        setupCloseButton()
        loadApps()
    }

    private fun setupRecyclerView() {
        appsAdapter = AppsAdapter(
            onAppClick = { app ->
                launchApp(app)
            },
            onAppLongClick = { _ ->
                // TODO: Show context menu for pinning/unpinning
            }
        )

        binding.rvApps.apply {
            adapter = appsAdapter
            layoutManager = if (preferenceManager.isGridLayoutEnabled()) {
                GridLayoutManager(context, 4)
            } else {
                LinearLayoutManager(context)
            }
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCloseButton() {
        binding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }

            // Close on background tap
            root.setOnClickListener {
                dismiss()
            }

            // Prevent closing when tapping on content
            root.findViewById<View>(android.R.id.content)?.setOnClickListener { }
        }
    }

    private fun loadApps() {
        binding.progressLoading.visibility = View.VISIBLE
        binding.rvApps.visibility = View.GONE

        // Since we can't use coroutines without LifecycleOwner, we'll use a simple approach
        Thread {
            val apps = runCatching {
                // Simplified app loading without coroutines
                val packageManager = context.packageManager
                val intent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
                    addCategory(android.content.Intent.CATEGORY_LAUNCHER)
                }
                
                val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
                val appsList = mutableListOf<AppInfo>()

                for (resolveInfo in resolveInfoList) {
                    try {
                        val activityInfo = resolveInfo.activityInfo
                        val packageName = activityInfo.packageName
                        val appName = resolveInfo.loadLabel(packageManager).toString()
                        val icon = resolveInfo.loadIcon(packageManager)

                        if (packageName != context.packageName) {
                            appsList.add(AppInfo(packageName, appName, icon))
                        }
                    } catch (e: Exception) {
                        continue
                    }
                }

                appsList.sortBy { it.appName.lowercase() }
                appsList
            }.getOrElse { emptyList() }

            // Update UI on main thread
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                allApps = apps
                appsAdapter.submitList(apps)
                binding.progressLoading.visibility = View.GONE
                binding.rvApps.visibility = View.VISIBLE
                
                if (apps.isEmpty()) {
                    binding.tvNoApps.visibility = View.VISIBLE
                } else {
                    binding.tvNoApps.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun filterApps(query: String) {
        val filteredApps = if (query.isBlank()) {
            allApps
        } else {
            val lowercaseQuery = query.lowercase()
            allApps.filter { app ->
                app.appName.lowercase().contains(lowercaseQuery) ||
                app.packageName.lowercase().contains(lowercaseQuery)
            }
        }

        appsAdapter.submitList(filteredApps)
        binding.tvNoApps.visibility = if (filteredApps.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun launchApp(app: AppInfo) {
        AppInfoUtils.launchApp(context, app.packageName)
        dismiss()
    }

    fun show() {
        if (isShowing) return

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START

        try {
            windowManager.addView(overlayView, layoutParams)
            isShowing = true

            // Focus search and show keyboard
            binding.etSearch.requestFocus()
            showKeyboard()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismiss() {
        if (!isShowing) return

        try {
            hideKeyboard()
            windowManager.removeView(overlayView)
            isShowing = false
            
            // Clear search
            binding.etSearch.setText("")
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
} 