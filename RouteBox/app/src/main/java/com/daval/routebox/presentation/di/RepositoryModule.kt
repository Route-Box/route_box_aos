package com.daval.routebox.presentation.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.daval.routebox.data.datasource.RemoteAuthDataSource
import com.daval.routebox.data.datasource.RemoteOpenApiDataSource
import com.daval.routebox.data.datasource.RemoteRouteDataSource
import com.daval.routebox.data.datasource.RemoteUserDataSource
import com.daval.routebox.data.repositoriyImpl.AuthRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.ReportRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.RouteRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.OpenApiRepositoryImpl
import com.daval.routebox.data.repositoriyImpl.UserRepositoryImpl
import com.daval.routebox.domain.repositories.AuthRepository
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.domain.repositories.ReportRepository
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.domain.repositories.UserRepository
import com.davl.routebox.data.datasource.RemoteReportDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
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