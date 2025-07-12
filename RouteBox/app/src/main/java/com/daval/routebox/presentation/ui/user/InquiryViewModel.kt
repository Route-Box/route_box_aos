package com.daval.routebox.presentation.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daval.routebox.domain.model.Inquiry
import com.daval.routebox.domain.model.InquiryList
import com.daval.routebox.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InquiryViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {
    private val _inquiry = MutableLiveData<Inquiry>(Inquiry(-1, "", ""))
    var inquiry: LiveData<Inquiry> = _inquiry

    val inquiryContent = MutableLiveData<String>("")
    val inquiryStatus = MutableLiveData<String>("")

    private val _inquiryHistoryList = MutableLiveData<InquiryList>(
        InquiryList(listOf(Inquiry(-1, "", "")))
    )
    var inquiryHistoryList: LiveData<InquiryList> = _inquiryHistoryList

    private val _btnEnabled = MutableLiveData<Boolean>()
    val btnEnabled: LiveData<Boolean> = _btnEnabled
}