package com.daval.routebox.domain.usecase

import android.net.Uri
import com.daval.routebox.domain.model.Comment
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.repositories.CommentRepository
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(routeId: Int): List<Comment> {
        return commentRepository.getComments(routeId).comments
    }
}