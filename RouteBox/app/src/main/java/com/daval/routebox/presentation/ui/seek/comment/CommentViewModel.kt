package com.daval.routebox.presentation.ui.seek.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.Comment
import com.daval.routebox.domain.usecase.comment.DeleteCommentUseCase
import com.daval.routebox.domain.usecase.comment.EditCommentUseCase
import com.daval.routebox.domain.usecase.comment.GetCommentsUseCase
import com.daval.routebox.domain.usecase.comment.PostCommentUseCase
import com.daval.routebox.domain.usecase.report.ReportCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val postCommentUseCase: PostCommentUseCase,
    private val editCommentUseCase: EditCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val reportCommentUseCase: ReportCommentUseCase
) : ViewModel() {
    private val _routeName = MutableLiveData<String>()
    val routeName: LiveData<String> = _routeName

    private val _routeId = MutableLiveData<Int>()
    val routeId: LiveData<Int> = _routeId

    val content = MutableLiveData<String>()

    private val _commentList = MutableLiveData<ArrayList<Comment>>(arrayListOf())
    val commentList: LiveData<ArrayList<Comment>> = _commentList

    private val _isEditMode = MutableLiveData<Boolean>(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    private var editingCommentId: Int? = null

    // 루트 이름으로 타이틀 초기화
    fun initTitle(title: String) {
        _routeName.value = title
    }

    // 루트id 초기화
    fun initRouteId(routeId: Int) {
        _routeId.value = routeId
    }

    // 수정할 댓글 내용 content에 대입
    fun setContentForEdit(commentId: Int) {
        val comment = _commentList.value?.find { it.commentId == commentId }
        if (comment != null) {
            content.value = comment.content  // 수정할 댓글 내용 삽입
            editingCommentId = commentId  // 현재 수정 중인 댓글 ID 저장
            _isEditMode.value = true  // 수정 모드 활성화
        }
    }

    // 댓글 조회
    fun getComments() {
        viewModelScope.launch {
            _commentList.value = ArrayList(getCommentsUseCase.invoke(routeId.value ?: -1))
            content.value = ""
            _isEditMode.value = false
        }
    }

    // 댓글 작성 또는 수정
    fun sendComment() {
        if (content.value.isNullOrBlank() || routeId.value == null) return

        if (_isEditMode.value == true && editingCommentId != null) {
            editComment(editingCommentId!!, content.value!!)
        } else {
            postComment()
        }
    }

    // 댓글 작성
    private fun postComment() {
        if (content.value.isNullOrBlank() || routeId.value == null) return
        viewModelScope.launch {
            val response = postCommentUseCase.invoke(routeId.value!!, content.value ?: "")

            if (response.isSuccess) { getComments() } // 성공 시 댓글 리스트 갱신
        }
    }

    // 댓글 수정
    private fun editComment(commentId: Int, newContent: String) {
        viewModelScope.launch {
            val response = editCommentUseCase.invoke(commentId, newContent)
            if (response.isSuccess) {
                val updatedList = ArrayList(_commentList.value ?: arrayListOf())
                val index = updatedList.indexOfFirst { it.commentId == commentId }
                if (index != -1) {
                    updatedList[index] = updatedList[index].copy(content = newContent)
                    _commentList.value = updatedList
                }

                resetToWriteMode()
            }
        }
    }

    // 댓글 삭제
    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            val response = deleteCommentUseCase.invoke(commentId)
            if (response.isSuccess) {
                val updatedList = ArrayList(_commentList.value ?: arrayListOf())
                updatedList.removeAll { it.commentId == commentId }
                _commentList.value = updatedList
            }
        }
    }

    // 댓글 신고
    fun reportComment(commentId: Int) {
        viewModelScope.launch {
            val response = reportCommentUseCase.invoke(commentId)
        }
    }

    // 수정 모드 해제 및 작성 모드로 복귀
    fun resetToWriteMode() {
        _isEditMode.value = false
        editingCommentId = null
        content.value = ""
    }
}
