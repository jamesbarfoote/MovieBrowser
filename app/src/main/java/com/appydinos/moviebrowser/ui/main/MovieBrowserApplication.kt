package com.appydinos.moviebrowser.ui.main

import android.app.Application
import android.os.Build
import android.os.Debug
import com.appydinos.moviebrowser.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MovieBrowserApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
