package com.daval.routebox.presentation.ui.auth

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.LoginRequest
import com.daval.routebox.domain.model.LoginResponse
import com.daval.routebox.domain.model.RefreshRequest
import com.daval.routebox.domain.model.RefreshResponse
import com.daval.routebox.domain.repositories.AuthRepository
import com.daval.routebox.domain.repositories.UserRepository
import com.daval.routebox.presentation.config.ApplicationClass.Companion.dsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _step = MutableLiveData<Int>()
    val step: LiveData<Int> = _step

    val nickname = MutableLiveData<String>()

    private val _birth = MutableLiveData<String>()
    val birth: LiveData<String> = _birth

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    private val _terms = MutableLiveData<Boolean>()
    val terms: LiveData<Boolean> = _terms

    // 유효한 닉네임
    private val _isValidNickname = MutableLiveData<Boolean>()
    val isValidNickname: LiveData<Boolean> = _isValidNickname

    // 닉네임 중복 확인
    private val _isAvailableNickname = MutableLiveData<Boolean?>()
    val isAvailableNickname: LiveData<Boolean?> = _isAvailableNickname

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    private val _refreshResponse = MutableLiveData<RefreshResponse?>()
    val refreshResponse: LiveData<RefreshResponse?> = _refreshResponse

    // 회원가입 성공
    private val _isSignupSuccess = MutableLiveData<Boolean>()
    val isSignupSuccess: LiveData<Boolean> = _isSignupSuccess

    init {
        _step.value = 1
        nickname.value = ""
        _birth.value = ""
        _gender.value = ""
        _terms.value = false
    }

    /** 로그인 */
    fun tryLogin(kakaoAccessToken: String) {
        viewModelScope.launch {
            // 로그인 진행
            val response = authRepository.postKakaoLogin(LoginRequest(kakaoAccessToken))
            // 토큰 정보 저장
            saveToken(response.accessToken.token, response.refreshToken.token)
            _loginResponse.value = response
        }
    }

    /** 토큰 재발급 */
    fun tryRefreshToken() {
        viewModelScope.launch {
            val response = authRepository.postRefreshToken(RefreshRequest(getSavedRefreshToken()))
            // 토큰 정보 저장
            saveToken(response.accessToken.token, response.refreshToken.token)
            _refreshResponse.value = response
        }
    }

    /** 닉네임 중복 확인 */
    private fun tryGetNicknameAvailability() {
        viewModelScope.launch {
            _isAvailableNickname.value = userRepository.getNicknameAvailability(nickname.value.toString()).isAvailable
        }
    }

    /** 회원가입 (내 정보 수정) */
    fun trySignup() {
        Log.d("AuthViewModel", "birth: ${_birth.value}")
        viewModelScope.launch {
            _isSignupSuccess.value = userRepository.signup(nickname.value!!, _birth.value!!, _gender.value!!).id != 0
        }
    }

    fun setStep(step: Int) {
        _step.value = step
    }

    fun setNickname(nickname: String) {
        this.nickname.value = nickname
        if (nickname.isEmpty()) {
            _isAvailableNickname.value = null
        }
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

    // 닉네임 중복 확인 버튼 클릭
    fun onClickNicknameDuplicationCheckBtn(view: View) {
        tryGetNicknameAvailability()
    }

    // 유효한 닉네임 확인
    fun setNicknameValidation() {
        _isValidNickname.value = nickname.value!!.matches(NICKNAME_REGEX) && nickname.value?.length!! > 1
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

    companion object {
        val NICKNAME_REGEX = "^[ㄱ-ㅣ가-힣a-zA-Z0-9]*$".toRegex() // 닉네임 정규식 - 한글, 영문, 숫자만 허용한 2~8 글자
    }
}