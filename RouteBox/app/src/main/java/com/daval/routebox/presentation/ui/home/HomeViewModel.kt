package com.daval.routebox.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daval.routebox.domain.repositories.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {
    private val _alarmStatus = MutableLiveData<Boolean>(false)
    val alarmStatus: LiveData<Boolean> = _alarmStatus

    // TODO: API 연동 후 삭제
    private val _recommendTitle = MutableLiveData<String>("따뜻한 6월엔 여기로 데이트 어때요? 테스트")
    val recommendTitle: LiveData<String> = _recommendTitle
}