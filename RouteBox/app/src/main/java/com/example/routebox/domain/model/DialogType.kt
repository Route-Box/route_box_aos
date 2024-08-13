package com.example.routebox.domain.model

enum class DialogType(val id: Int) {
    EDIT(0), // 수정
    DELETE(1), // 삭제
    REPORT(2), // 신고
    CHANGE_PUBLIC(3); // 공개 여부 변경

    companion object {
        fun getDialogTypeById(id: Int): DialogType? {
            return entries.find { it.id == id }
        }
    }
}