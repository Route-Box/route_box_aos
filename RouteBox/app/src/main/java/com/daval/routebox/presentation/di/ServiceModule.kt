package com.daval.routebox.presentation.di

import com.daval.routebox.data.remote.CommentApiService
import com.daval.routebox.data.remote.KakaoApiService
import com.daval.routebox.data.remote.OpenApiService
import com.daval.routebox.data.remote.ReportApiService
import com.daval.routebox.data.remote.auth.AnonymousApiService
import com.daval.routebox.data.remote.RouteApiService
import com.daval.routebox.data.remote.SeekApiService
import com.daval.routebox.data.remote.UserApiService
import com.daval.routebox.data.remote.auth.RefreshApiService
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

    /** 댓글 */
    @Provides
    @Singleton
    fun provideCommentService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : CommentApiService =
        retrofit.create(CommentApiService::class.java)

    /** 신고 */
    @Provides
    @Singleton
    fun provideReportService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : ReportApiService =
        retrofit.create(ReportApiService::class.java)

    /** 탐색 */
    @Provides
    @Singleton
    fun provideSeekService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : SeekApiService =
        retrofit.create(SeekApiService::class.java)

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