package com.example.petrov122_prs

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var context: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}