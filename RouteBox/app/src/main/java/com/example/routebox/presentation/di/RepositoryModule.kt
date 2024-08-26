package com.example.routebox.presentation.di

import com.example.routebox.data.datasource.RemoteAuthDataSource
import com.example.routebox.data.datasource.RemoteRouteDataSource
import com.example.routebox.data.repositoriyImpl.AuthRepositoryImpl
import com.example.routebox.data.repositoriyImpl.RouteRepositoryImpl
import com.example.routebox.domain.repositories.AuthRepository
import com.example.routebox.domain.repositories.RouteRepository
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

    @Provides
    fun provideRouteRepository(
        remoteRouteDataSource: RemoteRouteDataSource,
    ): RouteRepository = RouteRepositoryImpl(remoteRouteDataSource)
}