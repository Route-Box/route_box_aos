package com.example.routebox.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    private val _step = MutableLiveData<Int>()
    val step: LiveData<Int> = _step

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    private val _birth = MutableLiveData<String>()
    val birth: LiveData<String> = _birth

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    private val _terms = MutableLiveData<Boolean>()
    val terms: LiveData<Boolean> = _terms

    init {
        _step.value = 1
        _nickname.value = ""
        _birth.value = ""
        _gender.value = ""
        _terms.value = false
    }

    fun setStep(step: Int) {
        _step.value = step
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setBirth(birth: String) {
        _birth.value = birth
    }

    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun setTerms(terms: Boolean) {
        _terms.value = terms
    }
}