package com.example.routebox.presentation.di

import com.example.routebox.data.datasource.RemoteAuthDataSource
import com.example.routebox.data.datasource.RemoteReportDataSource
import com.example.routebox.data.datasource.RemoteRouteDataSource
import com.example.routebox.data.datasource.RemoteUserDataSource
import com.example.routebox.data.repositoriyImpl.AuthRepositoryImpl
import com.example.routebox.data.repositoriyImpl.ReportRepositoryImpl
import com.example.routebox.data.repositoriyImpl.RouteRepositoryImpl
import com.example.routebox.data.repositoriyImpl.UserRepositoryImpl
import com.example.routebox.domain.repositories.AuthRepository
import com.example.routebox.domain.repositories.ReportRepository
import com.example.routebox.domain.repositories.RouteRepository
import com.example.routebox.domain.repositories.UserRepository
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
    @Provides
    fun provideRouteRepository(
        remoteRouteDataSource: RemoteRouteDataSource,
    ): RouteRepository = RouteRepositoryImpl(remoteRouteDataSource)

    /** 신고 */
    @Provides
    fun provideReportRepository(
        remoteReportDataSource: RemoteReportDataSource
    ): ReportRepository = ReportRepositoryImpl(remoteReportDataSource)
}