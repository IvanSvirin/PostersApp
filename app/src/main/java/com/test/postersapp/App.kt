package com.test.postersapp

import android.app.Application
import com.test.postersapp.domain.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                viewModelModule,
                useCaseModule,
                repositoryModule,
                dataSourceModule,
                networkModule,
                cacheModule
            )
            androidContext(this@App)
        }

    }
}