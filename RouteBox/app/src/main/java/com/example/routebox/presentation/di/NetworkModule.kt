package com.example.routebox.presentation.di

import com.example.routebox.data.remote.auth.RefreshApiService
import com.example.routebox.presentation.config.Constants.BASE_URL
import com.example.routebox.presentation.config.XAccessTokenInterceptor
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
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AnonymousRetrofit

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
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()
}