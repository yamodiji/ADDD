package com.smartdrawer.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for MainActivity
 * Manages UI state and service status
 */
class MainViewModel : ViewModel() {

    private val _serviceRunning = MutableLiveData<Boolean>(false)
    val serviceRunning: LiveData<Boolean> = _serviceRunning

    private val _permissionGranted = MutableLiveData<Boolean>(false)
    val permissionGranted: LiveData<Boolean> = _permissionGranted

    fun setServiceRunning(isRunning: Boolean) {
        _serviceRunning.value = isRunning
    }

    fun setPermissionGranted(isGranted: Boolean) {
        _permissionGranted.value = isGranted
    }
}