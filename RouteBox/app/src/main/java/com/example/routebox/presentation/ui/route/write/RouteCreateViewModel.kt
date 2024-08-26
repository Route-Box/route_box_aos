package com.example.routebox.presentation.ui.route.write

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class RouteCreateViewModel: ViewModel() {
    private val _startDate = MutableLiveData<LocalDate>(TODAY)
    val startDate: LiveData<LocalDate> = _startDate

    private val _endDate = MutableLiveData<LocalDate>(TODAY)
    val endDate: LiveData<LocalDate> = _endDate

    private val _startTimePair = MutableLiveData<Pair<Int, Int>>()
    val startTimePair: LiveData<Pair<Int, Int>> = _startTimePair

    private val _endTimePair = MutableLiveData<Pair<Int, Int>>()
    val endTimePair: LiveData<Pair<Int, Int>> = _endTimePair

    private val _buttonActivation = MutableLiveData<Boolean>()
    val buttonActivation: LiveData<Boolean> = _buttonActivation

    fun updateDate(isStartDate: Boolean, date: LocalDate) {
        if (isStartDate) _startDate.value = date
        else _endDate.value = date
    }

    fun updateTime(isStartTime: Boolean, timePair: Pair<Int, Int>) {
        if (isStartTime) _startTimePair.value = timePair
        else _endTimePair.value = timePair
        updateButtonActivation()
    }

    // 버튼 활성화 여부 업데이트
    private fun updateButtonActivation() {
        _buttonActivation.value = _startTimePair.value != null && _endTimePair.value != null
    }
}