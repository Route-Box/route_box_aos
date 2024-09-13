package com.example.routebox.presentation.ui.common.report

import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.R

class ReportViewModel: ViewModel() {
    private var _feedReportCheckedReasons: ArrayList<CheckBox> = arrayListOf() // 체크한 신고 목록

    private val _isReportBtnEnable = MutableLiveData<Boolean>()
    val isReportBtnEnable: LiveData<Boolean> = _isReportBtnEnable

    private val _isEtcBtnChecked = MutableLiveData<Boolean>()
    val isEtcBtnChecked: LiveData<Boolean> = _isEtcBtnChecked

    val etcReasonDirectInput = MutableLiveData<String>() // '기타' 내용 직접 입력

    // 신고 사유 체크박스 상태 업데이트
    fun updateIsCheckedReportReason(checkBox: View) {
        val cb = checkBox as CheckBox
        Log.d("ReportViewModel", "clickedCheckBox: ${checkBox.text}, ${cb.isChecked}")
        _feedReportCheckedReasons.let {
            if (cb.isChecked) { // 체크 해제 처리
                it.add(cb)
            } else { // 체크 추가 처리
                it.remove(cb)
            }
        }
        checkIsReportButtonEnable() // 체크박스 변경될 때마다 버튼 활성화 상태 확인
    }

    // 신고 버튼 활성화 상태 체크
    fun checkIsReportButtonEnable() {
        _feedReportCheckedReasons.let { checkedReasons ->
            checkIsEtcCheckBoxChecked()
            if (isEtcBtnChecked.value == false) {  // '기타'가 선택되지 않았을 경우, 다른 체크박스가 하나라도 선택되면 활성화
                _isReportBtnEnable.value = checkedReasons.isNotEmpty()
                return
            }

            // '기타'가 선택되었을 경우 입력한 내용이 있는지 확인
            _isReportBtnEnable.value = !etcReasonDirectInput.value.isNullOrBlank()
        }
    }

    private fun checkIsEtcCheckBoxChecked() {
        _isEtcBtnChecked.value = _feedReportCheckedReasons.any { it.id == R.id.report_reason_etc_checkbox }
    }
}