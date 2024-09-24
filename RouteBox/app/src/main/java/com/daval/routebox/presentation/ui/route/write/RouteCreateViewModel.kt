package com.daval.routebox.presentation.ui.route.write

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import com.daval.routebox.presentation.utils.DateConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteCreateViewModel @Inject constructor(
    private val repository: RouteRepository
) : ViewModel() {
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

    /** 루트 생성 */
    fun tryCreateRoute() {
        viewModelScope.launch {
            repository.createRoute(
                DateConverter.convertDateAndTimeToUTCString(_startDate.value!!, _startTimePair.value!!),
                DateConverter.convertDateAndTimeToUTCString(_endDate.value!!, _endTimePair.value!!),
            )
        }
    }

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