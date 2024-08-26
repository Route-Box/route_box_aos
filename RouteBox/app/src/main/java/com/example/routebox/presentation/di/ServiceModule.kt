package com.example.routebox.presentation.di

import com.example.routebox.data.remote.auth.AnonymousApiService
import com.example.routebox.data.remote.RouteApiService
import com.example.routebox.data.remote.auth.RefreshApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    /** 익명 로그인 */
    @Provides
    @Singleton
    fun provideAnonymousService(@NetworkModule.AnonymousRetrofit retrofit: Retrofit) : AnonymousApiService =
        retrofit.create(AnonymousApiService::class.java)

    /** 토큰 재발급 */
    @Provides
    @Singleton
    fun provideRefreshService(@NetworkModule.AnonymousRetrofit retrofit: Retrofit) : RefreshApiService =
        retrofit.create(RefreshApiService::class.java)

    @Provides
    @Singleton
    fun provideRouteKakaoSearchService(@NetworkModule.BasicRetrofit retrofit: Retrofit): RouteApiService =
        retrofit.create(RouteApiService::class.java)
}