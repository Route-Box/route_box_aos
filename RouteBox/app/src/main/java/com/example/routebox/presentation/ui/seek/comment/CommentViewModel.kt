package com.example.routebox.presentation.ui.seek.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentViewModel: ViewModel() {

    //TODO: API가 나오면 서버 응답값에 맞게 변경
    private val _postTitle = MutableLiveData<String>()
    val postTitle: LiveData<String> = _postTitle

    val content = MutableLiveData<String>()

    private val _commentList = MutableLiveData<ArrayList<String>>(arrayListOf())
    val commentList: LiveData<ArrayList<String>> = _commentList

    //TODO: 필요한 데이터로 초기화하기
    fun initComment(title: String) {
        _postTitle.value = title
    }

    // 댓글 전송
    fun postComment() {
        if (content.value.isNullOrBlank()) return
        updateCommentList()
    }

    private fun updateCommentList() {
        val updatedList = _commentList.value ?: arrayListOf()
        updatedList.add(content.value!!)
        _commentList.value = updatedList
        Log.d("CommentViewModel", "commentList: ${commentList.value}")
        content.value = ""
    }
}