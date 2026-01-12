package com.example.mediakampus.data.network

import com.example.mediakampus.data.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userPreferences.authToken.first()
        }

        val requestBuilder = chain.request().newBuilder()

        // Skip header untuk endpoint auth
        if (!chain.request().url.encodedPath.contains("/auth/")) {
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}