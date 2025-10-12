package com.arttttt.calenda.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arttttt.calenda.metro.appGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WidgetRefreshReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.arttttt.calenda.ACTION_REFRESH_WIDGET"
    }

    private val scope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REFRESH_WIDGET) return

        val pendingResult = goAsync()

        scope.launch {
            try {
                context.appGraph.widgetUpdateManager.updateAllWidgets()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}