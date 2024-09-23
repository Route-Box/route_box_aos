package com.daval.routebox.presentation.config.interceptor

import com.daval.routebox.presentation.config.ApplicationClass.Companion.dsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class TokenRefreshInterceptor @Inject constructor(
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val accessToken: String? = runBlocking { dsManager.getAccessToken().first() }

        val newRequest = request.newBuilder()

        if (accessToken != null) {
            newRequest.addHeader("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(newRequest.build())
    }
}