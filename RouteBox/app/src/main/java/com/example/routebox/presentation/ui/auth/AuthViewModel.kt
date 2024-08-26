package com.example.routebox.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import com.example.routebox.domain.repositories.AuthRepository
import com.example.routebox.presentation.config.ApplicationClass.Companion.dsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    init {
        _step.value = 1
        _nickname.value = ""
        _birth.value = ""
        _gender.value = ""
        _terms.value = false
    }

    /** 로그인 */
    fun tryLogin(kakaoAccessToken: String) {
        viewModelScope.launch {
            // 로그인 진행
            val response = repository.postKakaoLogin(LoginRequest(kakaoAccessToken))
            // 토큰 정보 저장
            saveToken(response)
            _loginResponse.value = response
        }
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

    // 토큰 정보 앱 내에 저장
    private suspend fun saveToken(tokenResult: LoginResponse) {
        dsManager.saveAccessToken(tokenResult.accessToken.token)
        dsManager.saveRefreshToken(tokenResult.refreshToken.token)
    }

    // 앱 내에 저장된 토큰 정보 삭제
    private suspend fun deleteToken() {
        dsManager.clearTokens()
    }
}