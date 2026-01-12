package com.example.mediakampus

import android.app.Application

class MediaKampusApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}