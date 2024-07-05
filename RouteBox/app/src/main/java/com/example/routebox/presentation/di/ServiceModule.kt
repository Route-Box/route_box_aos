package com.example.routebox.presentation.di

import com.example.routebox.data.remote.AuthApiService
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
}