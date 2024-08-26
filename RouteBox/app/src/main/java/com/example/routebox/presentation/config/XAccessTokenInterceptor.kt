package com.example.routebox.presentation.config

import android.util.Log
import com.example.routebox.data.remote.auth.RefreshApiService
import com.example.routebox.presentation.config.ApplicationClass.Companion.dsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class XAccessTokenInterceptor @Inject constructor(
    private val apiService: RefreshApiService
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val accessToken: String? = runBlocking { dsManager.getAccessToken().first() }
        val refreshToken: String? = runBlocking { dsManager.getRefreshToken().first() }

        val newRequest = request.newBuilder()

        if (accessToken != null) {
            newRequest.addHeader("Authorization", "Bearer $accessToken")
        }

        val response = chain.proceed(newRequest.build())

        when (response.code) {
            400 -> {
                // Show Bad Request Error Message
            }
            401 -> {
                // Show Forbidden Message
            }
            403 -> {
                // Show Forbidden Message
                Log.d("Token", "403 액세스 토큰 만료")
                // 이전 토큰
                if (accessToken != null) {
                    Log.d("AccessToken", accessToken)
                    Log.d("RefreshToken", "$refreshToken")
                }

                //TODO: 재발급 api 호출
            }
            404 -> {
                // Show NotFound Message
            }
            // ... and so on
        }

        return response
    }
}