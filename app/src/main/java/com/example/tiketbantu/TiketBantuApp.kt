package com.example.tiketbantu

import android.app.Application
import com.example.tiketbantu.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TiketBantuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TiketBantuApp)
            modules(appModule)
        }
    }
}
