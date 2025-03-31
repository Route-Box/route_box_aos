package com.daval.routebox.presentation.di

import com.daval.routebox.data.datasource.RemoteCommentDataSource
import com.daval.routebox.data.remote.CommentApiService
import com.daval.routebox.data.remote.ReportApiService
import com.daval.routebox.data.datasource.RemoteReportDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DataSourceModule {

    /** 댓글 */
    @Provides
    fun provideRemoteCommentDataSource(
        commentApiService: CommentApiService,
    ): RemoteCommentDataSource = RemoteCommentDataSource(commentApiService)

    /** 신고 */
    @Provides
    fun provideRemoteReportDataSource(
        reportApiService: ReportApiService,
    ): RemoteReportDataSource = RemoteReportDataSource(reportApiService)
}