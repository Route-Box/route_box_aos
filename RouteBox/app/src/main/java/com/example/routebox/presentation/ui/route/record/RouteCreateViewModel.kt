package com.example.routebox.presentation.ui.route.record

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.presentation.ui.route.record.RouteCreateActivity.Companion.TODAY
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class RouteCreateViewModel: ViewModel() {
    private val _startDate = MutableLiveData<LocalDate>(TODAY)
    val startDate: LiveData<LocalDate> = _startDate

    private val _endDate = MutableLiveData<LocalDate>(TODAY)
    val endDate: LiveData<LocalDate> = _endDate

    init {
        _startDate.value = TODAY
        _endDate.value = TODAY
    }

    fun updateDate(isStartDate: Boolean, date: LocalDate) {
        if (isStartDate) _startDate.value = date
        else _endDate.value = date
    }
}