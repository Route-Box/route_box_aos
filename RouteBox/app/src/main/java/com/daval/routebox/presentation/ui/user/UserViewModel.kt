package com.daval.routebox.presentation.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daval.routebox.domain.model.User
import com.daval.routebox.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {
    private val _userInfo = MutableLiveData<User>(
        User(
            1, "", "닉", "성별", "생일",
            "소개", 1, "지역", "루트스타일"
        )
    )
    val userInfo: LiveData<User> = _userInfo
}