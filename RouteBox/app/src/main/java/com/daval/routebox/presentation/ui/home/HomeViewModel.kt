package com.daval.routebox.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daval.routebox.domain.model.Notification
import com.daval.routebox.domain.repositories.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {
    private val _notificationStatus = MutableLiveData<Boolean>(false)
    val notificationStatus: LiveData<Boolean> = _notificationStatus

    private val _recommendTitle = MutableLiveData<String>("")
    val recommendTitle: LiveData<String> = _recommendTitle

    private val _notificationList = MutableLiveData<ArrayList<Notification>>()
    val notificationList: LiveData<ArrayList<Notification>> = _notificationList
}