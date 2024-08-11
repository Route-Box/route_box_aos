package com.example.routebox.presentation.ui.route

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RouteViewModel: ViewModel() {
    private val _isTracking = MutableLiveData<Boolean>()
    val isTracking: LiveData<Boolean>
        get() = _isTracking

    init {
        _isTracking.value = false
    }

    fun setIsTracking() {
        _isTracking.value = !_isTracking.value!!
    }
}