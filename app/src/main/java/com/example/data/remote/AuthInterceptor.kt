package com.example.data.remote

import com.example.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        sessionManager.jwtToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }
        builder.header("Accept", "application/json")
        builder.header("User-Agent", "Jeevan-Mobile-Android/1.0")

        return chain.proceed(builder.build())
    }
}
