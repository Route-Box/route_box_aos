package com.daval.routebox.domain.model

enum class DialogType(val id: Int) {
    EDIT(0), // 수정
    DELETE(1), // 삭제
    REPORT(2), // 신고
    CHANGE_PUBLIC(3), // 공개 여부 변경
    CALL_ACTIVITY_DATA(4); // 활동 임시 저장 불러오기

    companion object {
        fun getDialogTypeById(id: Int): DialogType? {
            return entries.find { it.id == id }
        }
    }
}