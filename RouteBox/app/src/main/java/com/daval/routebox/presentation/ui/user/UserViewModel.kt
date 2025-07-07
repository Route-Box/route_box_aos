package com.daval.routebox.presentation.ui.user

import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daval.routebox.R
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

    private val _tempUserInfo = MutableLiveData<User>(
        User(
            1, "", "닉", "성별", "생일",
            "소개", 1, "지역", "루트스타일"
        )
    )
    val tempUserInfo: LiveData<User> = _tempUserInfo

    var selectedRouteId: Int = -1

    private var _deleteAccountCheckedReasons: ArrayList<CheckBox> = arrayListOf() // 체크한 탈퇴 목록

    private val _isDeleteBtnEnable = MutableLiveData<Boolean>()
    val isDeleteBtnEnable: LiveData<Boolean> = _isDeleteBtnEnable

    private val _isEtcBtnChecked = MutableLiveData<Boolean>()
    val isEtcBtnChecked: LiveData<Boolean> = _isEtcBtnChecked

    val etcReasonDirectInput = MutableLiveData<String>() // '기타' 내용 직접 입력

    // 탈퇴 사유 체크박스 상태 업데이트
    fun updateIsCheckedDeleteAccountReason(checkBox: View) {
        val cb = checkBox as CheckBox
        Log.d("UserViewModel", "clickedCheckBox: ${checkBox.text}, ${cb.isChecked}")
        _deleteAccountCheckedReasons.let {
            if (cb.isChecked) { // 체크 해제 처리
                it.add(cb)
            } else { // 체크 추가 처리
                it.remove(cb)
            }
        }
        checkIsDeleteButtonEnable() // 체크박스 변경될 때마다 버튼 활성화 상태 확인
    }

    // 탈퇴 버튼 활성화 상태 체크
    fun checkIsDeleteButtonEnable() {
        _deleteAccountCheckedReasons.let { checkedReasons ->
            checkIsEtcCheckBoxChecked()
            if (isEtcBtnChecked.value == false) {  // '기타'가 선택되지 않았을 경우, 다른 체크박스가 하나라도 선택되면 활성화
                _isDeleteBtnEnable.value = checkedReasons.isNotEmpty()
                return
            }

            // '기타'가 선택되었을 경우 입력한 내용이 있는지 확인
            _isDeleteBtnEnable.value = !etcReasonDirectInput.value.isNullOrBlank()
        }
    }

    private fun checkIsEtcCheckBoxChecked() {
        _isEtcBtnChecked.value = _deleteAccountCheckedReasons.any { it.id == R.id.report_reason_etc_checkbox }
    }
}