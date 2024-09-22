package com.example.routebox.presentation.config

import android.app.Application
import android.content.SharedPreferences
import com.example.routebox.BuildConfig
import com.example.routebox.presentation.di.NetworkModule
import com.example.routebox.presentation.utils.DataStoreManager
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidApp
class ApplicationClass : Application() {

    @Inject
    @NetworkModule.BasicRetrofit
    lateinit var basicRetrofit: Retrofit

    @Inject
    @NetworkModule.AnonymousRetrofit
    lateinit var anonymousRetrofit: Retrofit

    @Inject
    @NetworkModule.KakaoRetrofit
    lateinit var kakaoRetrofit: Retrofit

    @Inject
    @NetworkModule.OpenApiRetrofit
    lateinit var openApiRetrofit: Retrofit

    init {
        instance = this
    }

    // 코틀린의 전역변수 문법
    companion object {
        lateinit var instance: ApplicationClass
            private set

        val sSharedPreferences: SharedPreferences
            get() = instance.getSharedPreferences("RouteBox", MODE_PRIVATE)

        lateinit var dsManager: DataStoreManager

        // 버전
        const val VERSION = "1.0.0"
    }

    override fun onCreate() {
        super.onCreate()

        dsManager = DataStoreManager(applicationContext)

        // SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}