package com.anxiousflyer.peacefulflight

import android.app.Application
import com.anxiousflyer.peacefulflight.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PeacefulFlight : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PeacefulFlight)
            modules(appModule)
        }
    }
}

