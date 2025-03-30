package com.daval.routebox.presentation.di

import com.daval.routebox.domain.repositories.CommentRepository
import com.daval.routebox.domain.repositories.ReportRepository
import com.daval.routebox.domain.usecase.comment.DeleteCommentUseCase
import com.daval.routebox.domain.usecase.comment.EditCommentUseCase
import com.daval.routebox.domain.usecase.comment.GetCommentsUseCase
import com.daval.routebox.domain.usecase.comment.PostCommentUseCase
import com.daval.routebox.domain.usecase.report.ReportCommentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    /** 댓글 */
    // 댓글 조회
    @Provides
    fun provideGetCommentsUseCase(commentRepository: CommentRepository): GetCommentsUseCase =
        GetCommentsUseCase(commentRepository)

    // 댓글 작성
    @Provides
    fun providePostCommentUseCase(commentRepository: CommentRepository): PostCommentUseCase =
        PostCommentUseCase(commentRepository)

    // 댓글 수정
    @Provides
    fun provideEditCommentUseCase(commentRepository: CommentRepository): EditCommentUseCase =
        EditCommentUseCase(commentRepository)

    // 댓글 삭제
    @Provides
    fun provideDeleteCommentUseCase(commentRepository: CommentRepository): DeleteCommentUseCase =
        DeleteCommentUseCase(commentRepository)

    /** 신고 */
    // 댓글 신고
    @Provides
    fun provideReportCommentUseCase(reportRepository: ReportRepository): ReportCommentUseCase =
        ReportCommentUseCase(reportRepository)
}