package com.samduka.dukacred

import android.app.Application
import com.samduka.dukacred.app.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DukaCredApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DukaCredApp)
            modules(appModules)
        }
    }
}