package com.daval.routebox.domain.model

enum class DialogType(val id: Int) {
    EDIT(0), // 수정
    DELETE(1), // 삭제
    REPORT(2), // 신고
    CHANGE_PUBLIC(3), // 공개 여부 변경
    CALL_ACTIVITY_DATA(4), // 활동 임시 저장 불러오기
    BUY_ROUTE_DEFAULT(5), // 루트 구매하기
    BUY_ROUTE_SUCCESS(6), // 루트 구매하기 성공
    BUY_ROUTE_FAILURE(7); // 루트 구매하기 실패

    companion object {
        fun getDialogTypeById(id: Int): DialogType? {
            return entries.find { it.id == id }
        }
    }
}