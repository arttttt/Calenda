package com.arttttt.calenda.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppScope::class)
@Inject
class WidgetUpdateManagerImpl(
    private val context: Context,
) : WidgetUpdateManager {

    companion object {

        //private const val UPDATE_INTERVAL_MILLIS = 5L * 60L * 1000L // 5 minutes
        private const val UPDATE_INTERVAL_MILLIS = 1L * 60L * 1000L // 5 minutes

        private const val REQUEST_CODE = 1001
    }

    override suspend fun updateAllWidgets() {
        withContext(Dispatchers.IO) {
            try {
                val glanceManager = GlanceAppWidgetManager(context)
                val glanceIds = glanceManager.getGlanceIds(AgendaWidget::class.java)

                glanceIds.forEach { glanceId ->
                    AgendaWidget().update(context, glanceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun schedulePeriodicUpdates() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WidgetRefreshReceiver::class.java).apply {
            action = WidgetRefreshReceiver.ACTION_REFRESH_WIDGET
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + UPDATE_INTERVAL_MILLIS,
            UPDATE_INTERVAL_MILLIS,
            pendingIntent,
        )
    }

    override fun cancelPeriodicUpdates() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WidgetRefreshReceiver::class.java).apply {
            action = WidgetRefreshReceiver.ACTION_REFRESH_WIDGET
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.cancel(pendingIntent)
    }
}