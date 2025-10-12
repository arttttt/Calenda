package com.arttttt.calenda.widget

interface WidgetUpdateManager {

    suspend fun updateAllWidgets()
    fun schedulePeriodicUpdates()
    fun cancelPeriodicUpdates()
}