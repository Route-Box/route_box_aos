package com.daval.routebox.presentation.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.daval.routebox.data.datasource.RemoteAuthDataSource
import com.daval.routebox.data.datasource.RemoteCommentDataSource
import com.daval.routebox.data.datasource.RemoteHomeDataSource
import com.daval.routebox.data.datasource.RemoteOpenApiDataSource
import com.daval.routebox.data.datasource.RemoteRouteDataSource
import com.daval.routebox.data.datasource.RemoteSeekDataSource
import com.daval.routebox.data.datasource.RemoteUserDataSource
import com.daval.routebox.data.repositoriyImpl.AuthRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.CommentRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.ReportRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.RouteRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.OpenApiRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.SeekRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.UserRepositoryImpl
import com.daval.routebox.domain.repositories.AuthRepository
import com.daval.routebox.domain.repositories.CommentRepository
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.domain.repositories.ReportRepository
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.domain.repositories.SeekRepository
import com.daval.routebox.domain.repositories.UserRepository
import com.daval.routebox.data.datasource.RemoteReportDataSource
import com.daval.routebox.data.repositoriyImpl.HomeRepositoryImpl
import com.daval.routebox.domain.repositories.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    /** 홈 */
    @Provides
    fun provideHomeRepository(
        remoteHomeDataSource: RemoteHomeDataSource
    ): HomeRepository = HomeRepositoryImpl(remoteHomeDataSource)

    /** 인증 */
    @Provides
    fun provideAuthRepository(
        remoteAuthDataSource: RemoteAuthDataSource,
    ): AuthRepository = AuthRepositoryImpl(remoteAuthDataSource)

    /** 유저 */
    @Provides
    fun provideUserRepository(
        remoteUserDataSource: RemoteUserDataSource
    ): UserRepository = UserRepositoryImpl(remoteUserDataSource)

    /** 루트 */
    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    fun provideRouteRepository(
        remoteRouteDataSource: RemoteRouteDataSource,
    ): RouteRepository = RouteRepositoryImpl(remoteRouteDataSource)

    /** 댓글 */
    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    fun provideCommentRepository(
        remoteCommentDataSource: RemoteCommentDataSource,
    ): CommentRepository = CommentRepositoryImpl(remoteCommentDataSource)

    /** 탐색 */
    @Provides
    fun provideSeekRepository(
        remoteSeekDataSource: RemoteSeekDataSource
    ): SeekRepository = SeekRepositoryImpl(remoteSeekDataSource)

    /** 신고 */
    @Provides
    fun provideReportRepository(
        remoteReportDataSource: RemoteReportDataSource
    ): ReportRepository = ReportRepositoryImpl(remoteReportDataSource)

    /** Open API */
    @Provides
    fun provideOpenApiRepository(
        remoteOpenApiDataSource: RemoteOpenApiDataSource
    ): OpenApiRepository = OpenApiRepositoryImpl(remoteOpenApiDataSource)
}