package com.daval.routebox.presentation.di

import com.daval.routebox.data.remote.auth.RefreshApiService
import com.daval.routebox.presentation.config.Constants.BASE_URL
import com.daval.routebox.presentation.config.Constants.KAKAO_BASE_URL
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.config.interceptor.BaseInterceptor
import com.daval.routebox.presentation.config.interceptor.TokenRefreshInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // 인터셉터 O, 403 재발급 O
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BasicRetrofit

    // 인터셉터 X (로그인)
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AnonymousRetrofit

    // 인터셉터 O, 403 재발급 X
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RefreshRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OpenApiRetrofit

    @Provides
    @Singleton
    @BasicRetrofit
    fun provideBasicOkHttpClient(
        interceptor: HttpLoggingInterceptor,
        @BasicRetrofit authInterceptor: BaseInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @BasicRetrofit
    fun provideBasicRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @BasicRetrofit client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    @BasicRetrofit
    fun provideBasicTokenInterceptor(
        apiService: RefreshApiService
    ): BaseInterceptor {
        return BaseInterceptor(apiService)
    }

    @Provides
    @Singleton
    @AnonymousRetrofit
    fun provideAnonymousRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @AnonymousRetrofit client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    @AnonymousRetrofit
    fun provideAnonymousOkHttpClient(
        interceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)
            .build()

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshOkHttpClient(
        interceptor: HttpLoggingInterceptor,
        @RefreshRetrofit authInterceptor: TokenRefreshInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @RefreshRetrofit client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideTokenRefreshInterceptor(): TokenRefreshInterceptor {
        return TokenRefreshInterceptor()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Provides
    @Singleton
    @KakaoRetrofit
    fun provideKakaoRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    var gson = GsonBuilder().setLenient().create()
    @Provides
    @Singleton
    @OpenApiRetrofit
    fun provideOpenApiRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(OPEN_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
}