package com.example.routebox.presentation.config

import android.app.Application
import android.content.SharedPreferences
import com.example.routebox.presentation.di.NetworkModule
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidApp
class ApplicationClass: Application() {
    // 서버 주소
    val API_URL = Constants.BASE_URL

    @Inject
    @NetworkModule.InterceptorRetrofit
    lateinit var interceptorRetrofit: Retrofit

    @Inject
    @NetworkModule.BasicRetrofit
    lateinit var basicRetrofit: Retrofit

    init {
        instance = this
    }

    // 코틀린의 전역변수 문법
    companion object {
        lateinit var instance: ApplicationClass
            private set

        val sSharedPreferences: SharedPreferences
            get() = instance.getSharedPreferences("RouteBox", MODE_PRIVATE)

        // 버전
        const val VERSION = "1.0.4"

        // JWT Token Header 키 값
        const val X_ACCESS_TOKEN = "X_ACCESS_TOKEN"
        const val X_REFRESH_TOKEN = "X_REFRESH_TOKEN"
    }

    override fun onCreate() {
        super.onCreate()

    }
}