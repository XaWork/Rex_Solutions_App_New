package com.ajayasija.rexsolutions

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RexApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}