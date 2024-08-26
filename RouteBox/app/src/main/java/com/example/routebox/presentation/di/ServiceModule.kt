package com.example.routebox.presentation.di

import com.example.routebox.data.remote.AuthApiService
import com.example.routebox.data.remote.RouteApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    /** 인증 */
    @Provides
    @Singleton
    fun provideLoginService(@NetworkModule.BasicRetrofit retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideRouteKakaoSearchService(@NetworkModule.BasicRetrofit retrofit: Retrofit): RouteApiService =
        retrofit.create(RouteApiService::class.java)
}