package com.example.routebox.presentation.di

import com.example.routebox.data.remote.KakaoApiService
import com.example.routebox.data.remote.OpenApiService
import com.example.routebox.data.remote.auth.AnonymousApiService
import com.example.routebox.data.remote.RouteApiService
import com.example.routebox.data.remote.UserApiService
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
    fun provideRefreshService(@NetworkModule.RefreshRetrofit retrofit: Retrofit) : RefreshApiService =
        retrofit.create(RefreshApiService::class.java)

    /** 유저 */
    @Provides
    @Singleton
    fun provideUserService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : UserApiService =
        retrofit.create(UserApiService::class.java)

    /** 루트 */
    @Provides
    @Singleton
    fun provideRouteService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : RouteApiService =
        retrofit.create(RouteApiService::class.java)

    /** 카카오 **/
    @Provides
    @Singleton
    fun provideKakaoService(@NetworkModule.KakaoRetrofit retrofit: Retrofit): KakaoApiService =
        retrofit.create(KakaoApiService::class.java)

    /** Open Api **/
    @Provides
    @Singleton
    fun provideOpenApiService(@NetworkModule.OpenApiRetrofit retrofit: Retrofit): OpenApiService =
        retrofit.create(OpenApiService::class.java)
}