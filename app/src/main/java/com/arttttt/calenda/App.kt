package com.arttttt.calenda

import android.app.Application
import com.arttttt.calenda.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class App : Application() {

    val appGraph by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        appGraph.widgetUpdateManager.cancelPeriodicUpdates()
        appGraph.widgetUpdateManager.schedulePeriodicUpdates()
    }
}