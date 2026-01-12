package com.example.mediakampus

import android.content.Context
import com.example.mediakampus.data.datastore.UserPreferences
import com.example.mediakampus.data.network.ApiService
import com.example.mediakampus.data.network.AuthInterceptor
import com.example.mediakampus.data.repository.MediaKampusRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

interface AppContainer {
    val repository: MediaKampusRepository
    val userPreferences: UserPreferences
}

class DefaultAppContainer(context: Context) : AppContainer {

    private val baseUrl = "http://192.168.100.10:8080/"

    override val userPreferences = UserPreferences(context)

    private val authInterceptor = AuthInterceptor(userPreferences)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val repository: MediaKampusRepository by lazy {
        MediaKampusRepository(apiService)
    }
}