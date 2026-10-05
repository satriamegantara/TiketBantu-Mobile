package com.example.tiketbantu

import android.app.Application
import com.example.tiketbantu.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import com.example.tiketbantu.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TiketBantuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val koin = startKoin {
            androidContext(this@TiketBantuApp)
            modules(appModule)
        }.koin

        // Inisialisasi awal database agar migrasi & 4 kategori langsung siap dipakai
        koin.get<CoroutineScope>().launch {
            try {
                koin.get<AppDatabase>().categoryDao().countCategories()
            } catch (_: Exception) {}
        }
    }
}
