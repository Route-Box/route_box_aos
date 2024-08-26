package com.example.routebox.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import com.example.routebox.domain.model.RefreshRequest
import com.example.routebox.domain.model.RefreshResponse
import com.example.routebox.domain.repositories.AuthRepository
import com.example.routebox.presentation.config.ApplicationClass.Companion.dsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    private val _refreshResponse = MutableLiveData<RefreshResponse?>()
    val refreshResponse: LiveData<RefreshResponse?> = _refreshResponse

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
            saveToken(response.accessToken.token, response.refreshToken.token)
            _loginResponse.value = response
        }
    }

    /** 토큰 재발급 */
    fun tryRefreshToken() {
        viewModelScope.launch {
            val response = repository.postRefreshToken(RefreshRequest(getSavedRefreshToken()))
            // 토큰 정보 저장
            saveToken(response.accessToken.token, response.refreshToken.token)
            _refreshResponse.value = response
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

    /** 토큰 */
    // 앱 내 저장된 토큰 정보 가져오기
    private fun getSavedAccessToken(): String = runBlocking {
        dsManager.getAccessToken().first().orEmpty()
    }

    private fun getSavedRefreshToken(): String = runBlocking {
        dsManager.getRefreshToken().first().orEmpty()
    }

    // 토큰 정보 앱 내에 저장
    private suspend fun saveToken(accessToken: String, refreshToken: String) {
        dsManager.saveAccessToken(accessToken)
        dsManager.saveRefreshToken(refreshToken)
    }

    // 앱 내에 저장된 토큰 정보 삭제
    private suspend fun deleteToken() {
        dsManager.clearTokens()
    }
}