package com.example.calmflight

import android.app.Application
import com.example.calmflight.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CalmFlightApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CalmFlightApp)
            modules(appModule)
        }
    }
}

